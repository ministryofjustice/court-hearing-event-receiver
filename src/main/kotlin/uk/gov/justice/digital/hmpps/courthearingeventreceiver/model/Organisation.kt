package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class Organisation(
  @field:NotBlank
  @JsonProperty("name")
  val name: String,

  @JsonProperty("address")
  val address: Address?,
)
