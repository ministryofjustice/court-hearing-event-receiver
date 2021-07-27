package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class Offence(

  @NotBlank
  @JsonProperty("id")
  val id: String,

  @NotBlank
  @JsonProperty("offenceDefinitionId")
  val offenceDefinitionId: String,

  @NotBlank
  @JsonProperty("offenceCode")
  val offenceCode: String,

  @NotBlank
  @JsonProperty("offenceTitle")
  val offenceTitle: String,

  @NotBlank
  @JsonProperty("wording")
  val wording: String,

  @JsonProperty("offenceLegislation")
  val offenceLegislation: String? = null

)
