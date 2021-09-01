package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

enum class TelemetryEventType(val eventName: String) {
  COURT_HEARING_EVENT_RECEIVED("PiCCourtHearingEventReceived"),
  COURT_HEARING_DELETE_EVENT_RECEIVED("PiCCourtHearingDeleteEventReceived")
}
