package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.InitiationCode

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProsecutionCase(

  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:NotNull
  @JsonProperty("initiationCode")
  val initiationCode: InitiationCode,

  @field:NotNull
  @JsonProperty("prosecutionCaseIdentifier")
  val prosecutionCaseIdentifier: ProsecutionCaseIdentifier,

  @field:Valid
  @field:NotEmpty
  @JsonProperty("defendants")
  val defendants: List<Defendant> = emptyList(),

  @JsonProperty("caseStatus")
  val caseStatus: String?,

  @field:Valid
  val caseMarkers: List<CaseMarker> = emptyList(),

)
