package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageNotifier
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService

@RestController
class EventController(
  @Autowired
  private val messageNotifier: MessageNotifier,
  @Autowired
  private val telemetryService: TelemetryService,
) {

  @RequestMapping(value = ["/hearing/{id}", "/hearing/{id}/"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  fun postEvent(
    @PathVariable(required = false) id: String,
    @Valid @RequestBody
    hearingEvent: HearingEvent,
  ) {
    log.info("Received hearing event payload id: %s, path variable id: %s".format(hearingEvent.hearing.id, id))
    trackAndSendEvent(HearingEventType.CONFIRMED_OR_UPDATED, hearingEvent)
  }

  @RequestMapping(value = ["/hearing/{id}/result"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  fun postResultEvent(
    @PathVariable(required = false) id: String,
    @Valid @RequestBody
    hearingEvent: HearingEvent,
  ) {
    log.info("Received hearing event payload id: %s, path variable id: %s".format(hearingEvent.hearing.id, id))
    trackAndSendEvent(HearingEventType.RESULTED, hearingEvent)
  }

  @DeleteMapping(value = ["/hearing/{id}/delete"])
  @ResponseStatus(HttpStatus.OK)
  fun deleteEvent(@PathVariable(required = false) id: String) {
    log.info("Received hearing delete request id: %s".format(id))
    telemetryService.trackEvent(
      TelemetryEventType.COURT_HEARING_DELETE_EVENT_RECEIVED,
      mapOf("id" to id),
    )
    // TODO - how to send a delete event for a hearing ?
  }

  private fun trackAndSendEvent(hearingEventType: HearingEventType, hearingEvent: HearingEvent) {
    val hearing = hearingEvent.hearing
    val courtCode = hearing.courtCentre.code.substring(0, 5)
    telemetryService.trackEvent(
      hearingEventType.getTelemetryEventType(),
      mapOf(
        "courtCode" to courtCode,
        "hearingId" to hearing.id,
        "caseId" to hearing.prosecutionCases.getOrNull(0)?.id,
        "caseUrn" to hearing.prosecutionCases.getOrNull(0)?.prosecutionCaseIdentifier?.caseURN,
      ),
    )
    messageNotifier.send(hearingEventType, hearingEvent)
  }

  companion object {
    private val log = LoggerFactory.getLogger(EventController::class.java)
  }
}
