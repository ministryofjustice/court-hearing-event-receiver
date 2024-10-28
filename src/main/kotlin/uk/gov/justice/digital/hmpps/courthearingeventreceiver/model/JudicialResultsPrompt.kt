package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class JudicialResultsPrompt(

  val courtExtract:String?,

  @JsonProperty("isDurationEndDate")
  val isDurationEndDate: Boolean?,

  @JsonProperty("isFinancialImposition")
  val isFinancialImposition: Boolean?,

  @field:NotBlank
  @JsonProperty("judicialResultPromptTypeId")
  val judicialResultPromptTypeId: String,

  @field:NotBlank
  @JsonProperty("label")
  val label: String,

  @JsonProperty("promptReference")
  val promptReference: String?,

  @JsonProperty("promptSequence")
  val promptSequence: Number?,

  @JsonProperty("type")
  val type: String?,

  @JsonProperty("value")
  val value: String?,
)