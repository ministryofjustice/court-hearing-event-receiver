package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Ethnicity(

  val observedEthnicityDescription: String?,

  val selfDefinedEthnicityDescription: String?,
)
