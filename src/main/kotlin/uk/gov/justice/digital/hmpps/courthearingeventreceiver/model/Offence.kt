package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class Offence(

  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:NotBlank
  @JsonProperty("offenceDefinitionId")
  val offenceDefinitionId: String,

  @field:NotBlank
  @JsonProperty("offenceCode")
  val offenceCode: String,

  @field:NotBlank
  @JsonProperty("offenceTitle")
  val offenceTitle: String,

  @field:NotBlank
  @JsonProperty("wording")
  val wording: String,

  @JsonProperty("offenceLegislation")
  val offenceLegislation: String? = null,

  @JsonProperty("listingNumber")
  val listingNumber: Int? = null

)
