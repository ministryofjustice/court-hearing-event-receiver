package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProsecutionCaseIdentifier(
  @field:NotBlank
  @JsonProperty("prosecutionAuthorityCode")
  val prosecutionAuthorityCode: String,

  @field:NotBlank
  @JsonProperty("prosecutionAuthorityId")
  val prosecutionAuthorityId: String,

  @field:NotBlank
  @JsonProperty("caseURN")
  val caseURN: String,
)
