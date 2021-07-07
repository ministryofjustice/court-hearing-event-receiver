package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Hearing(
  val id: String,
  val courtCentre: CourtCentre,
  val hearingDays: List<HearingDay>
)
