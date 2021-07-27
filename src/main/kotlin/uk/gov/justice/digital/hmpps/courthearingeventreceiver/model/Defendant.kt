package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Defendant(

  @NotBlank
  @JsonProperty("id")
  val id: String,

  @NotEmpty
  @JsonProperty("offences")
  val offences: List<Offence> = emptyList(),

  // This looks to be simply the same value as the id in the parent ProsecutionCase.
  @NotBlank
  @JsonProperty("prosecutionCaseId")
  val prosecutionCaseId: String,

  @JsonProperty("personDefendant")
  val personDefendant: PersonDefendant?,

  @JsonProperty("legalEntityDefendant")
  val legalEntityDefendant: LegalEntityDefendant?,

  @JsonProperty("masterDefendantId")
  val masterDefendantId: String,

  @JsonProperty("pncId")
  val pncId: String?,

  @JsonProperty("croNumber")
  val croNumber: String?
)
