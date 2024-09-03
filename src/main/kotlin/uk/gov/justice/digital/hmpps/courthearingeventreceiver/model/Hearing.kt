package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JurisdictionType

@JsonIgnoreProperties(ignoreUnknown = true)
data class Hearing(

  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:NotNull
  @field:Valid
  @JsonProperty("courtCentre")
  val courtCentre: CourtCentre,

  @field:NotNull
  @JsonProperty("type")
  val type: HearingType,

  @field:NotNull
  @JsonProperty("jurisdictionType")
  val jurisdictionType: JurisdictionType,

  @field:Valid
  @field:NotEmpty
  @JsonProperty("hearingDays")
  val hearingDays: List<HearingDay> = emptyList(),

  @field:Valid
  @field:NotEmpty
  @JsonProperty("prosecutionCases")
  val prosecutionCases: List<ProsecutionCase> = emptyList(),
)
