package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Alias(
  @JsonProperty("title")
  val title: String,
  @JsonProperty("firstName")
  val firstName: String?,
  @JsonProperty("middleName")
  val middleName: String?,
  @JsonProperty("lastName")
  val lastName: String?,
  @JsonProperty("legalEntityName")
  val legalEntityName: String?,
)
