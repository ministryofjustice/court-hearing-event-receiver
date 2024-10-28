package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LJA(
  @JsonProperty("ljaCode")
  val ljaCode: String?,
  @JsonProperty("ljaName")
  val ljaName: String,
)