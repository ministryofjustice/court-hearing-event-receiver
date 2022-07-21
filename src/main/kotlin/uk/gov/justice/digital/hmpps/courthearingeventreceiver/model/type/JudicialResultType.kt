package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class JudicialResultType(
  @NotBlank
  @JsonProperty("description")
  val description: String,

  @field:NotBlank
  @JsonProperty("id")
  val id: String
)
