package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type

import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType

enum class HearingEventType(val description: String) {
  CONFIRMED_OR_UPDATED("ConfirmedOrUpdated"),
  RESULTED("Resulted"),
  DELETED("Deleted"),
  ;

  fun getTelemetryEventType(): TelemetryEventType = when (this) {
    CONFIRMED_OR_UPDATED -> TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED
    RESULTED -> TelemetryEventType.COURT_HEARING_RESULT_EVENT_RECEIVED
    DELETED -> TelemetryEventType.COURT_HEARING_DELETE_EVENT_RECEIVED
  }
}
