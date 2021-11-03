package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageNotifier
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.io.File

@ExtendWith(MockitoExtension::class)
internal class EventControllerTest {

  private lateinit var hearingEvent: HearingEvent
  private lateinit var eventController: EventController
  private val mapper by lazy {
    ObjectMapper()
      .registerModule(JavaTimeModule())
      .registerModule(KotlinModule())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  @Mock
  lateinit var messageNotifier: MessageNotifier

  @Mock
  lateinit var telemetryService: TelemetryService

  @BeforeEach
  fun beforeEach() {
    val str = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    hearingEvent = mapper.readValue(str, HearingEvent::class.java)
    eventController = EventController(messageNotifier, telemetryService, includedCourts = INCLUDED_COURTS)
  }

  @Test
  fun `when receive update message for included court then send message`() {

    val id = hearingEvent.hearing.id

    eventController.postEvent(id, hearingEvent)

    verify(messageNotifier).send(hearingEvent)
    verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED, mapOf("courtCode" to NORTH_TYNESIDE, "id" to id))
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive update message for excluded court then do not send message`() {
    eventController = EventController(messageNotifier, telemetryService, includedCourts = emptySet())

    val id = hearingEvent.hearing.id

    eventController.postEvent(id, hearingEvent)

    verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED, mapOf("courtCode" to NORTH_TYNESIDE, "id" to id))
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive result message for included court then send message`() {

    val id = hearingEvent.hearing.id

    eventController.postResultEvent(id, hearingEvent)

    verify(messageNotifier).send(hearingEvent)
    verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED, mapOf("courtCode" to NORTH_TYNESIDE, "id" to id))
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  @Test
  fun `when receive result message for excluded court then do not send message`() {
    eventController = EventController(messageNotifier, telemetryService, includedCourts = emptySet())

    val id = hearingEvent.hearing.id

    eventController.postResultEvent(id, hearingEvent)

    verify(telemetryService).trackEvent(TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED, mapOf("courtCode" to NORTH_TYNESIDE, "id" to id))
    verifyNoMoreInteractions(telemetryService, messageNotifier)
  }

  companion object {
    @JvmField
    var NORTH_TYNESIDE = "B10JQ"
    @JvmField
    var LEICESTER = "B33HU"

    @JvmField
    var INCLUDED_COURTS = setOf(NORTH_TYNESIDE, LEICESTER)
  }
}
