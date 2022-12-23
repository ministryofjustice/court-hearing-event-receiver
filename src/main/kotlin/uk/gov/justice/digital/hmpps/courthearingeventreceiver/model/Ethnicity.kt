package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Ethnicity(

  @JsonProperty("observedEthnicityDescription")
  val observedEthnicityDescription: String?,

  @JsonProperty("selfDefinedEthnicityDescription")
  val selfDefinedEthnicityDescription: String?
)
