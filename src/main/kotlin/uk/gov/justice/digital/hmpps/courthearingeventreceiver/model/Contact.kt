package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Contact(

  @JsonProperty("home")
  val home: String?,

  @JsonProperty("mobile")
  val mobile: String?,

  @JsonProperty("work")
  val work: String?
)
