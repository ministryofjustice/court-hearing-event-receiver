package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageNotifier
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.io.File

const val NORTH_TYNESIDE = "B10JQ"

@ExtendWith(MockitoExtension::class)
internal class EventControllerTest {
  private lateinit var hearingEvent: HearingEvent
  private lateinit var eventController: EventController
  private val mapper by lazy {
    ObjectMapper()
      .registerModule(JavaTimeModule())
      .registerModule(KotlinModule.Builder().build())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  private val hearingId = "59cb14a6-e8de-4615-9c9d-94fa5ef81ad2"
  private val expectedProperties = mapOf(
    "courtCode" to NORTH_TYNESIDE,
    "hearingId" to hearingId,
    "caseId" to "1d1861ed-e18c-429d-bad0-671802f9cdba",
    "caseUrn" to "80GD8183221",
  )

  @Mock
  lateinit var messageNotifier: MessageNotifier

  @Mock
  lateinit var telemetryService: TelemetryService

  @BeforeEach
  fun beforeEach() {
    val str = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    hearingEvent = mapper.readValue(str, HearingEvent::class.java)
    eventController =
      EventController(messageNotifier, telemetryService)
  }

  @Test
  fun `when receive update message for included court then send message`() {
    eventController.postEvent(hearingId, hearingEvent)

    verify(messageNotifier).send(HearingEventType.CONFIRMED_OR_UPDATED, hearingEvent)
    verify(telemetryService).trackEvent(
      TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED,
      expectedProperties,
    )
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive update message with no prosecutionCases for included court then send message and track event`() {
    val caselessHearingEvent = HearingEvent(hearing = hearingEvent.hearing.copy(prosecutionCases = emptyList()))

    eventController.postEvent(hearingId, caselessHearingEvent)

    verify(messageNotifier).send(HearingEventType.CONFIRMED_OR_UPDATED, caselessHearingEvent)
    verify(telemetryService).trackEvent(
      TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED,
      mapOf(
        "courtCode" to NORTH_TYNESIDE,
        "hearingId" to hearingId,
        "caseId" to null,
        "caseUrn" to null,
      ),
    )
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive result message for included court then send message`() {
    eventController.postResultEvent(hearingId, hearingEvent)

    verify(messageNotifier).send(HearingEventType.RESULTED, hearingEvent)
    verify(telemetryService).trackEvent(
      TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED,
      expectedProperties,
    )
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive result message for included court and allow list disabled then send message`() {
    eventController =
      EventController(messageNotifier, telemetryService)

    eventController.postResultEvent(hearingId, hearingEvent)

    verify(messageNotifier).send(HearingEventType.RESULTED, hearingEvent)
    verify(telemetryService).trackEvent(
      TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED,
      expectedProperties,
    )
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive result message for excluded court and allow list disabled then send message`() {
    eventController =
      EventController(messageNotifier, telemetryService)

    eventController.postResultEvent(hearingId, hearingEvent)

    verify(messageNotifier).send(HearingEventType.RESULTED, hearingEvent)
    verify(telemetryService).trackEvent(
      TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED,
      expectedProperties,
    )
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }
}
