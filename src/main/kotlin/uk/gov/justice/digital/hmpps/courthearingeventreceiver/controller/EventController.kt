package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageNotifier
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import javax.validation.Valid

@Api(value = "Hearing Event")
@RestController
class EventController(
  @Autowired
  private val messageNotifier: MessageNotifier,
  @Autowired
  private val telemetryService: TelemetryService,
  @Value("#{'\${included-court-codes}'.split(',')}")
  private val includedCourts: Set<String> = emptySet(),
  @Value("\${feature.use-included-courts-list}")
  private val useIncludedCourtsList: Boolean = false,
) {

  @ApiOperation(value = "Endpoint to receive hearing events of CONFIRMED/UPDATE type")
  @RequestMapping(value = ["/hearing/{id}"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  fun postEvent(@PathVariable(required = false) id: String, @Valid @RequestBody hearingEvent: HearingEvent) {
    log.info("Received hearing event payload id: %s, path variable id: %s".format(hearingEvent.hearing.id, id))
    trackAndSendEvent(TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED, hearingEvent)
  }

  @ApiOperation(value = "Endpoint to receive hearing events of RESULT type")
  @RequestMapping(value = ["/hearing/{id}/result"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  fun postResultEvent(@PathVariable(required = false) id: String, @Valid @RequestBody hearingEvent: HearingEvent) {
    log.info("Received hearing event payload id: %s, path variable id: %s".format(hearingEvent.hearing.id, id))
    trackAndSendEvent(TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED, hearingEvent)
  }

  @ApiOperation(value = "Endpoint to delete a hearing")
  @DeleteMapping(value = ["/hearing/{id}/delete"])
  @ResponseStatus(HttpStatus.OK)
  fun deleteEvent(@PathVariable(required = false) id: String) {
    log.info("Received hearing delete request id: %s".format(id))
    telemetryService.trackEvent(
      TelemetryEventType.COURT_HEARING_DELETE_EVENT_RECEIVED,
      mapOf("id" to id)
    )
    // TODO - how to send a delete event for a hearing ?
  }

  private fun trackAndSendEvent(eventType: TelemetryEventType, hearingEvent: HearingEvent) {
    val hearing = hearingEvent.hearing
    val courtCode = hearing.courtCentre.code.substring(0, 5)
    telemetryService.trackEvent(
      eventType,
      mapOf("courtCode" to courtCode, "id" to hearing.id)
    )
    if (!useIncludedCourtsList || includedCourts.contains(courtCode)) {
      messageNotifier.send(hearingEvent)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(EventController::class.java)
  }
}
