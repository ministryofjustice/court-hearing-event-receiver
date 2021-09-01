package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.io.File
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
class EventControllerIntTest : IntegrationTestBase() {

  lateinit var hearingEvent: HearingEvent

  @Autowired
  lateinit var mapper: ObjectMapper

  @Autowired
  lateinit var sqs: AmazonSQSAsync

  @MockBean
  lateinit var telemetryService: TelemetryService

  @BeforeEach
  fun beforeEach() {
    val str = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    hearingEvent = mapper.readValue(str, HearingEvent::class.java)
  }

  @Nested
  inner class ConfirmedUpdatedEndpoint {
    @Test
    fun whenPostToEventEndpointWithRequiredRole_thenReturn204NoContent_andPushToTopic() {

      postEvent(
        hearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE"))
      )
        .exchange()
        .expectStatus().isOk

      // Verify new thing received at topic
      val messages = sqs.receiveMessageAsync("http://localhost:4566/000000000000/test-queue")
        .get(5, TimeUnit.SECONDS)
      assertThat(messages.messages.size).isEqualTo(1)
      assertThat(messages.messages[0].body).contains("59cb14a6-e8de-4615-9c9d-94fa5ef81ad2")

      val expectedMap = mapOf("id" to "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2", "courtCode" to "B10JQ00")
      verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_EVENT_RECEIVED, expectedMap)
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

    private fun postEvent(hearingEvent: HearingEvent, token: String) =
      webTestClient
        .post()
        .uri(String.format(UPDATED_PATH, hearingEvent.hearing.id))
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer $token")
        .body(Mono.just(hearingEvent), HearingEvent::class.java)
  }

  @Nested
  inner class DeleteEndpoint {
    @Test
    fun whenPostToHearingDeleteEndpointWithRequiredRole_thenReturn204NoContent_andPushToTopic() {

      deleteEvent(
        hearingEvent,
        jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE")),
        DELETE_PATH
      )
        .exchange()
        .expectStatus().isOk

      // TODO - when we understand the nature of the DELETE message to be posted, then verify the SNS posting

      val expectedMap = mapOf("id" to "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2", "courtCode" to "B10JQ00")
      verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_EVENT_RECEIVED, expectedMap)
    }

    @Test
    fun whenPostToHearingDeleteEndpointWithoutRequiredRole_thenReturn403Forbidden() {

      deleteEvent(hearingEvent, jwtHelper.createJwt("common-platform-events"), DELETE_PATH)
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun whenPostToEventEndpointWithBadToken_thenReturn401Unauthorized() {
      val token = "bad_token"
      deleteEvent(hearingEvent, token, DELETE_PATH)
        .exchange()
        .expectStatus().isUnauthorized
    }

    private fun deleteEvent(hearingEvent: HearingEvent, token: String, id: String) =
      webTestClient
        .delete()
        .uri(String.format(DELETE_PATH, hearingEvent.hearing.id))
        .header("Authorization", "Bearer $token")
  }

  companion object {
    const val UPDATED_PATH: String = "/hearing/%s"
    const val DELETE_PATH: String = "/hearing/%s/delete"
  }
}
