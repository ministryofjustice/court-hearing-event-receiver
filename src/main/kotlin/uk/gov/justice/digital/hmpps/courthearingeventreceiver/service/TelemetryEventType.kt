package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

enum class TelemetryEventType(val eventName: String) {
  COURT_HEARING_UPDATE_EVENT_RECEIVED("PiCCourtHearingUpdateEventReceived"),
  COURT_HEARING_RESULT_EVENT_RECEIVED("PiCCourtHearingResultEventReceived"),
  COURT_HEARING_DELETE_EVENT_RECEIVED("PiCCourtHearingDeleteEventReceived"),
  COMMON_PLATFORM_EVENT_OBSERVED("PiCCommonPlatformFieldObserved")
}
