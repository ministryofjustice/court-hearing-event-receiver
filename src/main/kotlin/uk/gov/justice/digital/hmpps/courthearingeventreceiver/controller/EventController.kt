package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

@Api(value = "Hearing Event")
@RestController
class EventController(
  @Autowired
  private val messageNotifier: MessageNotifier,
  @Autowired
  private val telemetryService: TelemetryService
) {

  @ApiOperation(value = "Endpoint to receive hearing events")
  @RequestMapping(value = ["/hearing/{id}"], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.ACCEPTED)
  fun postEvent(@PathVariable(required = false) id: String, @RequestBody hearingEvent: HearingEvent) {
    log.info("Received hearing event payload id: %s, path variable id: %s".format(hearingEvent.hearing.id, id))
    val hearing = hearingEvent.hearing
    telemetryService.trackEvent(
      TelemetryEventType.COURT_HEARING_EVENT_RECEIVED,
      mapOf("courtCode" to hearing.courtCentre.code, "id" to hearing.id)
    )
    messageNotifier.send(hearingEvent)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
