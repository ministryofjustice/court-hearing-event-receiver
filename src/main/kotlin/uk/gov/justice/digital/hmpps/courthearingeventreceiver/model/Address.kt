package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
  @NotBlank
  @JsonProperty("address1")
  val address1: String,
  @JsonProperty("address2")
  val address2: String?,
  @JsonProperty("address3")
  val address3: String?,
  @JsonProperty("address4")
  val address4: String?,
  @JsonProperty("address5")
  val address5: String?,
  @JsonProperty("postcode")
  val postcode: String?
)
