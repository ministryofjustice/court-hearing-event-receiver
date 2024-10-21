package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Defendant(

  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:Valid
  @field:NotEmpty
  @JsonProperty("offences")
  val offences: List<Offence> = emptyList(),

  // This looks to be simply the same value as the id in the parent ProsecutionCase.
  @field:NotBlank
  @JsonProperty("prosecutionCaseId")
  val prosecutionCaseId: String,

  // There will be a personDefendant OR legalEntityDefendant
  @field:Valid
  @JsonProperty("personDefendant")
  val personDefendant: PersonDefendant?,

  @field:Valid
  @JsonProperty("legalEntityDefendant")
  val legalEntityDefendant: LegalEntityDefendant?,

  @JsonProperty("masterDefendantId")
  val masterDefendantId: String?,

  @JsonProperty("pncId")
  val pncId: String?,

  @JsonProperty("croNumber")
  val croNumber: String?,

  @JsonProperty("isYouth")
  val isYouth: boolean,
)
