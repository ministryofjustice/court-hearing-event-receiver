package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JurisdictionType
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

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
  val jurisdictionType: JurisdictionType = JurisdictionType.MAGISTRATES,

  @field:Valid
  @field:NotEmpty
  @JsonProperty("hearingDays")
  val hearingDays: List<HearingDay> = emptyList(),

  @field:Valid
  @field:NotEmpty
  @JsonProperty("prosecutionCases")
  val prosecutionCases: List<ProsecutionCase> = emptyList()
)
