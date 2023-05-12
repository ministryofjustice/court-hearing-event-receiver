package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.Gender
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.InitiationCode
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JurisdictionType
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

@ExtendWith(MockitoExtension::class)
internal class ModelParserTest {

  private lateinit var mapper: ObjectMapper

  @BeforeEach
  fun beforeEach() {
    mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
    mapper.registerModule(KotlinModule())
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  @Test
  fun `tests the parsing of JSON to the model`() {
    val path = "src/test/resources/json/hearing-1.json"
    val content = Files.readString(Paths.get(path))

    val hearing = mapper.readValue(content, Hearing::class.java)

    assertThat(hearing.id).isEqualTo("8bbb4fe3-a899-45c7-bdd4-4ee25ac5a83f")
    assertThat(hearing.courtCentre.code).isEqualTo("B05KP00")
    assertThat(hearing.type.description).isEqualTo("Sentence")
    assertThat(hearing.jurisdictionType).isSameAs(JurisdictionType.CROWN)
    assertThat(hearing.hearingDays.size).isEqualTo(1)
    assertThat(hearing.hearingDays[0].sittingDay).isEqualTo(LocalDateTime.of(2019, Month.JANUARY, 28, 10, 30))
    assertThat(hearing.hearingDays[0].listingSequence).isEqualTo(0)
    assertThat(hearing.prosecutionCases.size).isEqualTo(1)
    assertThat(hearing.prosecutionCases[0].id).isEqualTo("b7417f11-49d8-482d-b516-ba4135d38d0d")
    assertThat(hearing.prosecutionCases[0].caseMarkers).isEmpty()
    assertThat(hearing.prosecutionCases[0].initiationCode).isSameAs(InitiationCode.C)
    assertThat(hearing.prosecutionCases[0].caseStatus).isEqualTo("READY_FOR_REVIEW")
    assertThat(hearing.prosecutionCases[0].defendants.size).isEqualTo(2)
    assertThat(hearing.prosecutionCases[0].defendants[0].prosecutionCaseId).isEqualTo("b7417f11-49d8-482d-b516-ba4135d38d0d")
    assertThat(hearing.prosecutionCases[0].defendants[0].masterDefendantId).isEqualTo("0ab7c3e5-eb4c-4e3f-b9e6-b9e78d3ea199")
    assertThat(hearing.prosecutionCases[0].defendants[0].id).isEqualTo("0ab7c3e5-eb4c-4e3f-b9e6-b9e78d3ea199")
    assertThat(hearing.prosecutionCases[0].defendants[1].masterDefendantId).isEqualTo("903c4c54-f667-4770-8fdf-1adbb5957c25")
    assertThat(hearing.prosecutionCases[0].defendants[1].id).isEqualTo("903c4c54-f667-4770-8fdf-1adbb5957c25")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences.size).isEqualTo(2)
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].offenceLegislation).isEqualTo("Contrary to section 20 of the Offences Against the    Person Act 1861.")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].judicialResults[0].isConvictedResult).isEqualTo(false)
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].judicialResults[0].label).isEqualTo("Adjournment")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].judicialResults[0].judicialResultTypeId).isEqualTo("06b0c2bf-3f98-46ed-ab7e-56efaf9ecced")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].plea?.pleaValue).isEqualTo("GUILTY")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].plea?.pleaDate).isEqualTo("2019-01-01")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[1].plea).isEqualTo(null)
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].verdict?.verdictType?.description).isEqualTo("verdictTypeValue")
    assertThat(hearing.prosecutionCases[0].defendants[0].offences[0].verdict?.verdictDate).isEqualTo("2019-01-01")
    assertThat(hearing.prosecutionCases[0].defendants[0].personDefendant?.personDetails?.firstName).isEqualTo("Trevion")
    assertThat(hearing.prosecutionCases[0].defendants[0].personDefendant?.personDetails?.lastName).isEqualTo("McCullough")
    assertThat(hearing.prosecutionCases[0].defendants[0].personDefendant?.personDetails?.gender).isSameAs(Gender.MALE)
    assertThat(hearing.prosecutionCases[0].defendants[0].personDefendant?.personDetails?.dateOfBirth).isEqualTo(LocalDate.of(1983, Month.FEBRUARY, 28))
    assertThat(hearing.prosecutionCases[0].defendants[1].personDefendant?.personDetails?.ethnicity?.observedEthnicityDescription).isEqualTo("observedEthnicityDescription")
    assertThat(hearing.prosecutionCases[0].defendants[1].personDefendant?.personDetails?.ethnicity?.selfDefinedEthnicityDescription).isEqualTo("selfDefinedEthnicityDescription")
  }
}
