package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type

enum class HearingEventType(val description: String) {
  CONFIRMED_OR_UPDATED("ConfirmedOrUpdated"),
  RESULTED("Resulted"),
  UNKNOWN("Unknown")
}
