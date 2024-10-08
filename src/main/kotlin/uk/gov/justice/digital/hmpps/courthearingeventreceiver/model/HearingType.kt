package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class HearingType(
  @NotBlank
  @JsonProperty("id")
  val id: String,

  @NotBlank
  @JsonProperty("description")
  val description: String,

  @JsonProperty("welshDescription")
  val welshDescription: String? = null,
)
