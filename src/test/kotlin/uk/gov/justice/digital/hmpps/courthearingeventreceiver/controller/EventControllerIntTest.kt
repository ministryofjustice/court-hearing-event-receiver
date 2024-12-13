package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.io.File

@ActiveProfiles("test")
class EventControllerIntTest : IntegrationTestBase() {

  lateinit var hearingEvent: HearingEvent

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @MockBean
  lateinit var telemetryService: TelemetryService

  @BeforeEach
  override fun beforeEach() {
    val str = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    hearingEvent = objectMapper.readValue(str, HearingEvent::class.java)
  }

  @Nested
  inner class ConfirmedUpdatedEndpoint {

    @Test
    fun whenPostToEventEndpointWithRequiredRole_thenReturn200NoContent_andPushToTopic() {
      postEvent(
        hearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
      )
        .exchange()
        .expectStatus().isOk

      val messages = courtCaseEventsQueue?.sqsClient?.receiveMessage(ReceiveMessageRequest.builder().queueUrl(courtCaseEventsQueue?.queueUrl!!).build())!!.get()
      assertThat(messages.messages().size).isEqualTo(1)
      val message: SQSMessage = objectMapper.readValue(messages.messages()[0].body(), SQSMessage::class.java)

      assertThat(message.message).contains("59cb14a6-e8de-4615-9c9d-94fa5ef81ad2") // this is the hearing ID
      assertThat(message.message).contains("Adjournment")
      assertThat(message.message).contains("isYouth")
      assertThat(message.message).contains("\"lja\":{\"ljaCode\":\"2577\",\"ljaName\":\"South West London Magistrates' Court\"}}")
      assertThat(message.message).contains("\"judicialResultPrompts\":[{\"courtExtract\":\"Y\",\"isDurationEndDate\":true,\"isFinancialImposition\":false,\"judicialResultPromptTypeId\":\"20fe3e69-c7d6-4f72-8b77-13c70c1f986d\",\"label\":\"Number of days to abstain from consuming any alcohol\",\"promptReference\":\"numberOfDaysToAbstainFromConsumingAnyAlcohol\",\"promptSequence\":100,\"type\":\"INT\",\"value\":\"120\"}]}]")
      assertThat(message.messageAttributes.messageType.type).isEqualTo("String")
      assertThat(message.messageAttributes.messageType.value).isEqualTo("COMMON_PLATFORM_HEARING")

      assertThat(message.messageAttributes.hearingEventType.value).isEqualTo(HearingEventType.CONFIRMED_OR_UPDATED.description)

      val expectedMap = mapOf(
        "courtCode" to "B10JQ",
        "hearingId" to "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2",
        "caseId" to "1d1861ed-e18c-429d-bad0-671802f9cdba",
        "caseUrn" to "80GD8183221",
      )
      verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED, expectedMap)
    }

    @Test
    fun whenPostToEventEndpointWithoutRequiredRole_thenReturn403Forbidden() {
      postEvent(hearingEvent, jwtHelper.createJwt("common-platform-events"))
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun whenPostToEventEndpointWithBadToken_thenReturn401Unauthorized() {
      val token = "bad_token"
      postEvent(hearingEvent, token)
        .exchange()
        .expectStatus().isUnauthorized
    }
  }

  @Nested
  inner class ResultEndpoint {
    @Test
    fun whenPostToEndpointWithRequiredRole_thenReturn200NoContent_andPushToTopic() {
      postEvent(
        hearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
        RESULT_PATH,
      )
        .exchange()
        .expectStatus().isOk

      val messages = courtCaseEventsQueue?.sqsClient?.receiveMessage(ReceiveMessageRequest.builder().queueUrl(courtCaseEventsQueue?.queueUrl!!).build())!!.get()
      assertThat(messages.messages().size).isEqualTo(1)
      val message: SQSMessage = objectMapper.readValue(messages.messages()[0].body(), SQSMessage::class.java)

      assertThat(message.message).contains("59cb14a6-e8de-4615-9c9d-94fa5ef81ad2")

      val expectedMap = mapOf(
        "courtCode" to "B10JQ",
        "hearingId" to "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2",
        "caseId" to "1d1861ed-e18c-429d-bad0-671802f9cdba",
        "caseUrn" to "80GD8183221",
      )
      verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED, expectedMap)
    }

    @Test
    fun whenPostToEndpointWithoutRequiredRole_thenReturn403Forbidden() {
      postEvent(hearingEvent, jwtHelper.createJwt("common-platform-events"))
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun whenPostToEndpointWithBadToken_thenReturn401Unauthorized() {
      val token = "bad_token"
      postEvent(hearingEvent, token)
        .exchange()
        .expectStatus().isUnauthorized
    }

    private fun postEvent(hearingEvent: HearingEvent, token: String) =
      webTestClient
        .post()
        .uri(String.format(UPDATE_PATH, hearingEvent.hearing.id))
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer $token")
        .body(Mono.just(hearingEvent), HearingEvent::class.java)
  }

  @Nested
  inner class DeleteEndpoint {
    @Test
    fun whenPostToHearingDeleteEndpointWithRequiredRole_thenReturn200NoContent_andDoNotPushToTopic() {
      deleteEvent(
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
        hearingEvent.hearing.id,
      )
        .exchange()
        .expectStatus().isOk

      // TODO - when we understand the nature of the DELETE message to be posted, then verify the SNS posting

      val messages = courtCaseEventsQueue?.sqsClient?.receiveMessage(ReceiveMessageRequest.builder().queueUrl(courtCaseEventsQueue?.queueUrl!!).build())!!.get()
      assertThat(messages.messages().size).isEqualTo(0)

      val expectedMap = mapOf("id" to "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2")
      verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_DELETE_EVENT_RECEIVED, expectedMap)
    }

    @Test
    fun whenPostToHearingDeleteEndpointWithoutRequiredRole_thenReturn403Forbidden() {
      deleteEvent(jwtHelper.createJwt("common-platform-events"), hearingEvent.hearing.id)
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun whenPostToEventEndpointWithBadToken_thenReturn401Unauthorized() {
      val token = "bad_token"
      deleteEvent(token, hearingEvent.hearing.id)
        .exchange()
        .expectStatus().isUnauthorized
    }

    private fun deleteEvent(token: String, id: String) =
      webTestClient
        .delete()
        .uri(String.format(DELETE_PATH, id))
        .header("Authorization", "Bearer $token")
  }

  @Nested
  inner class ValidatedEndpoint {

    @Test
    fun whenPostToEventEndpointWithRequiredRole_thenReturn200NoContent_andPushToTopic() {
      val courtCentre = hearingEvent.hearing.courtCentre.copy(id = "", code = "   ")
      val hearing = hearingEvent.hearing.copy(courtCentre = courtCentre)
      val invalidHearingEvent = HearingEvent(hearing = hearing)

      postEvent(
        invalidHearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
      )
        .exchange()
        .expectStatus().isBadRequest
    }

    @Test
    fun whenPostToEventEndpointWithTrailingSlashAndRequiredRole_thenReturn200NoContent_andPushToTopic() {
      val courtCentre = hearingEvent.hearing.courtCentre.copy(id = "", code = "   ")
      val hearing = hearingEvent.hearing.copy(courtCentre = courtCentre)
      val invalidHearingEvent = HearingEvent(hearing = hearing)

      postEvent(
        invalidHearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
        UPDATE_PATH_WITH_TRAILING_SLASH,
      )
        .exchange()
        .expectStatus().isBadRequest
    }
  }

  private fun postEvent(hearingEvent: HearingEvent, token: String, pathFormat: String = UPDATE_PATH) =
    webTestClient
      .post()
      .uri(String.format(pathFormat, hearingEvent.hearing.id))
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer $token")
      .body(Mono.just(hearingEvent), HearingEvent::class.java)

  companion object {
    const val UPDATE_PATH: String = "/hearing/%s"
    const val UPDATE_PATH_WITH_TRAILING_SLASH: String = "/hearing/%s/"
    const val RESULT_PATH: String = "/hearing/%s/result"
    const val DELETE_PATH: String = "/hearing/%s/delete"
  }

  data class SQSMessage(
    @JsonProperty("Message")
    val message: String,
    @JsonProperty("MessageAttributes")
    val messageAttributes: MessageAttributes,
  )
  data class MessageAttributes(
    val messageType: MessageAttribute,
    val hearingEventType: MessageAttribute,
  )

  data class MessageAttribute(
    @JsonProperty("Type")
    val type: String,
    @JsonProperty("Value")
    val value: String,
  )
}
