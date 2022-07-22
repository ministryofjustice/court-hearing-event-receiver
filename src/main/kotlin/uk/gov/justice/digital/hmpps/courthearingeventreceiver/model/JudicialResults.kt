package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JudicialResultType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class JudicialResults(
  @JsonProperty("isConvictedResult")
  val isConvictedResult: Boolean,

  @field:NotBlank
  @JsonProperty("label")
  val label: String,

  @field:NotNull
  @JsonProperty("type")
  val type: JudicialResultType,
)
