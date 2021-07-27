package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JurisdictionType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingType
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class Hearing(

  @NotNull
  @JsonProperty("id")
  val id: String,

  @NotNull
  @JsonProperty("courtCentre")
  val courtCentre: CourtCentre,

  @NotNull
  @JsonProperty("type")
  val type: HearingType,

  @NotNull
  @JsonProperty("jurisdictionType")
  val jurisdictionType: JurisdictionType = JurisdictionType.MAGISTRATES,

  @JsonProperty("hearingDays")
  val hearingDays: List<HearingDay> = emptyList(),

  @JsonProperty("prosecutionCases")
  val prosecutionCases: List<ProsecutionCase> = emptyList()
)
