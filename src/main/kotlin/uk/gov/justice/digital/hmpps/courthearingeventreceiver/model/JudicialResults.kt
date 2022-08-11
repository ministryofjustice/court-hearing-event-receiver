package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class JudicialResults(
  @JsonProperty("isConvictedResult")
  val isConvictedResult: Boolean,

  @field:NotBlank
  @JsonProperty("label")
  val label: String,

  @JsonProperty("judicialResultTypeId")
  val judicialResultTypeId: String?,
)
