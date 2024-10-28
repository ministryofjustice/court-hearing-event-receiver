package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class JudicialResults(
  @JsonProperty("isConvictedResult")
  val isConvictedResult: Boolean,

  @field:NotBlank
  @JsonProperty("label")
  val label: String,

  val judicialResultTypeId: String?,

  @JsonProperty("resultText")
  val resultText: String?,

  @JsonProperty("orderedDate")
  val orderedDate: LocalDate?,

  @JsonProperty("judicialResultPrompts")
  val judicialResultPrompts: List<JudicialResultsPrompt> = emptyList(),
)
