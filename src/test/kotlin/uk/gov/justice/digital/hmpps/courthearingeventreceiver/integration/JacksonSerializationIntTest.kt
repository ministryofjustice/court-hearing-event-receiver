package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.Defendant
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import java.io.File

/**
 * Integration test to verify Jackson 3.x serialization/deserialization behavior.
 * This test specifically validates that the custom Defendant deserializer properly handles
 * missing fields (isPncMissing, isCroMissing, isYouthMissing) which caused a production incident.
 */
@ActiveProfiles("test")
class JacksonSerializationIntTest : IntegrationTestBase() {

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Test
  fun `should deserialize hearing event with all defendant fields present`() {
    val json = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)

    val hearingEvent = objectMapper.readValue(json, HearingEvent::class.java)

    assertThat(hearingEvent).isNotNull
    assertThat(hearingEvent.hearing.prosecutionCases).isNotEmpty

    val defendant = hearingEvent.hearing.prosecutionCases[0].defendants[0]

    // Verify all required fields are present
    assertThat(defendant.id).isEqualTo("ac24a1be-939b-49a4-a524-21a3d228f8bc")
    assertThat(defendant.pncId).isEqualTo("20020073319Z")
    assertThat(defendant.croNumber).isEqualTo("SF05/482703J")
    assertThat(defendant.isYouth).isFalse()

    // Critical fields that caused production incident
    assertThat(defendant.isPncMissing).isFalse()
    assertThat(defendant.isCroMissing).isFalse()
    assertThat(defendant.isYouthMissing).isFalse()
  }

  @Test
  fun `should deserialize hearing event with missing pnc, cro, and isYouth fields and set flags correctly`() {
    val json = File("src/test/resources/json/court-application-missing-fields.json").readText(Charsets.UTF_8)

    val hearingEvent = objectMapper.readValue(json, HearingEvent::class.java)

    val defendant = hearingEvent.hearing.prosecutionCases[0].defendants[0]

    // When fields are missing from JSON, the missing flags should be true
    assertThat(defendant.pncId).isNull()
    assertThat(defendant.croNumber).isNull()
    assertThat(defendant.isYouth).isNull()

    // Critical: These flags should be set to true when fields are missing
    assertThat(defendant.isPncMissing).isTrue()
    assertThat(defendant.isCroMissing).isTrue()
    assertThat(defendant.isYouthMissing).isTrue()
  }

  @Test
  fun `should serialize and deserialize defendant maintaining all fields`() {
    val json = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    val originalEvent = objectMapper.readValue(json, HearingEvent::class.java)
    val originalDefendant = originalEvent.hearing.prosecutionCases[0].defendants[0]

    // Serialize back to JSON
    val serialized = objectMapper.writeValueAsString(originalEvent)

    // Deserialize again
    val deserializedEvent = objectMapper.readValue(serialized, HearingEvent::class.java)
    val deserializedDefendant = deserializedEvent.hearing.prosecutionCases[0].defendants[0]

    // Verify all critical fields are preserved through serialization cycle
    assertThat(deserializedDefendant.id).isEqualTo(originalDefendant.id)
    assertThat(deserializedDefendant.pncId).isEqualTo(originalDefendant.pncId)
    assertThat(deserializedDefendant.croNumber).isEqualTo(originalDefendant.croNumber)
    assertThat(deserializedDefendant.isYouth).isEqualTo(originalDefendant.isYouth)
    assertThat(deserializedDefendant.isPncMissing).isEqualTo(originalDefendant.isPncMissing)
    assertThat(deserializedDefendant.isCroMissing).isEqualTo(originalDefendant.isCroMissing)
    assertThat(deserializedDefendant.isYouthMissing).isEqualTo(originalDefendant.isYouthMissing)
  }

  @Test
  fun `should handle explicit isPncMissing flag in JSON`() {
    // Test case where JSON explicitly sets isPncMissing=true even though pncId is present
    val json = """
      {
        "id": "test-id",
        "prosecutionCaseId": "case-id",
        "offences": [],
        "personDefendant": null,
        "legalEntityDefendant": null,
        "masterDefendantId": null,
        "pncId": "12345",
        "isPncMissing": true,
        "croNumber": null,
        "isCroMissing": true,
        "isYouth": false,
        "isYouthMissing": false
      }
    """.trimIndent()

    val defendant = objectMapper.readValue(json, Defendant::class.java)

    // When explicit flag is provided, it should be respected
    assertThat(defendant.isPncMissing).isTrue()
    assertThat(defendant.isCroMissing).isTrue()
    assertThat(defendant.isYouthMissing).isFalse()
    assertThat(defendant.pncId).isEqualTo("12345")
  }

  @Test
  fun `should handle defendant with null personDefendant and legalEntityDefendant`() {
    val json = """
      {
        "id": "test-id",
        "prosecutionCaseId": "case-id",
        "offences": [],
        "personDefendant": null,
        "legalEntityDefendant": null,
        "masterDefendantId": null,
        "pncId": null,
        "croNumber": null,
        "isYouth": null
      }
    """.trimIndent()

    val defendant = objectMapper.readValue(json, Defendant::class.java)

    assertThat(defendant.personDefendant).isNull()
    assertThat(defendant.legalEntityDefendant).isNull()
    // When fields are present but null, the missing flags should be FALSE
    // because node.has() returns true (the field exists in JSON)
    assertThat(defendant.isPncMissing).isFalse()
    assertThat(defendant.isCroMissing).isFalse()
    assertThat(defendant.isYouthMissing).isFalse()
  }

  @Test
  fun `should handle defendant with empty offences list`() {
    val json = """
      {
        "id": "test-id",
        "prosecutionCaseId": "case-id",
        "offences": [],
        "personDefendant": null,
        "legalEntityDefendant": null,
        "masterDefendantId": "master-id",
        "pncId": "PNC123",
        "croNumber": "CRO456",
        "isYouth": true
      }
    """.trimIndent()

    val defendant = objectMapper.readValue(json, Defendant::class.java)

    assertThat(defendant.offences).isEmpty()
    assertThat(defendant.isPncMissing).isFalse()
    assertThat(defendant.isCroMissing).isFalse()
    assertThat(defendant.isYouthMissing).isFalse()
  }

  @Test
  fun `should deserialize defendant from production-like JSON without isPncMissing field`() {
    // This simulates the production scenario where the JSON doesn't contain isPncMissing
    // but the deserializer should infer it based on whether pncId is present
    val json = """
      {
        "id": "production-id",
        "prosecutionCaseId": "prod-case-id",
        "offences": [
          {
            "id": "offence-1",
            "offenceDefinitionId": "def-1",
            "offenceCode": "CODE1",
            "offenceTitle": "Test Offence",
            "wording": "Test wording"
          }
        ],
        "personDefendant": {
          "personDetails": {
            "firstName": "John",
            "lastName": "Doe",
            "dateOfBirth": "1990-01-01",
            "gender": "MALE"
          }
        },
        "legalEntityDefendant": null,
        "masterDefendantId": "master-prod-id",
        "pncId": null,
        "croNumber": "PROD-CRO",
        "isYouth": false
      }
    """.trimIndent()

    val defendant = objectMapper.readValue(json, Defendant::class.java)

    // When pncId is present but null, asString() returns empty string ""
    assertThat(defendant.pncId).isEmpty()
    // isPncMissing should be false because the field exists in JSON (node.has() returns true)
    assertThat(defendant.isPncMissing).isFalse()
    assertThat(defendant.croNumber).isNotNull()
    assertThat(defendant.isCroMissing).isFalse()
    assertThat(defendant.isYouth).isFalse()
    assertThat(defendant.isYouthMissing).isFalse()
    assertThat(defendant.personDefendant).isNotNull()
    assertThat(defendant.offences).hasSize(1)
  }

  @Test
  fun `should handle complete real-world hearing event JSON`() {
    // Load the actual test resource used in integration tests
    val json = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)

    // This should not throw any exceptions
    val hearingEvent = objectMapper.readValue(json, HearingEvent::class.java)

    // Verify the entire object graph is properly deserialized
    assertThat(hearingEvent.hearing).isNotNull()
    assertThat(hearingEvent.hearing.id).isNotBlank()
    assertThat(hearingEvent.hearing.courtCentre).isNotNull()
    assertThat(hearingEvent.hearing.prosecutionCases).isNotEmpty()

    val prosecutionCase = hearingEvent.hearing.prosecutionCases[0]
    assertThat(prosecutionCase.defendants).isNotEmpty()

    val defendant = prosecutionCase.defendants[0]
    assertThat(defendant.offences).isNotEmpty()

    // Serialize and verify no data loss
    val serialized = objectMapper.writeValueAsString(hearingEvent)
    assertThat(serialized).contains("isPncMissing")
    assertThat(serialized).contains("isCroMissing")
    assertThat(serialized).contains("isYouthMissing")
  }

  @Test
  fun `should preserve all defendant fields when converting through ObjectMapper`() {
    val json = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    val hearingEvent = objectMapper.readValue(json, HearingEvent::class.java)

    // Convert to generic map and back
    val asMap = objectMapper.convertValue(hearingEvent, Map::class.java)
    val backToEvent = objectMapper.convertValue(asMap, HearingEvent::class.java)

    val originalDefendant = hearingEvent.hearing.prosecutionCases[0].defendants[0]
    val convertedDefendant = backToEvent.hearing.prosecutionCases[0].defendants[0]

    // Verify all fields survived the conversion
    assertThat(convertedDefendant.isPncMissing).isEqualTo(originalDefendant.isPncMissing)
    assertThat(convertedDefendant.isCroMissing).isEqualTo(originalDefendant.isCroMissing)
    assertThat(convertedDefendant.isYouthMissing).isEqualTo(originalDefendant.isYouthMissing)
  }
}
