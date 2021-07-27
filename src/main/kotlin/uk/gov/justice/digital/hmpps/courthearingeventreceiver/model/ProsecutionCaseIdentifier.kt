package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProsecutionCaseIdentifier(
  @NotBlank
  @JsonProperty("prosecutionAuthorityCode")
  val prosecutionAuthorityCode: String,

  @NotBlank
  @JsonProperty("prosecutionAuthorityId")
  val prosecutionAuthorityId: String,

  @NotBlank
  @JsonProperty("caseURN")
  val caseURN: String
)
