package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.InitiationCode
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProsecutionCase(

  @NotBlank
  @JsonProperty("id")
  val id: String,

  @NotNull
  @JsonProperty("initiationCode")
  val initiationCode: InitiationCode,

  @NotNull
  @JsonProperty("prosecutionCaseIdentifier")
  val prosecutionCaseIdentifier: ProsecutionCaseIdentifier,

  @NotEmpty
  @JsonProperty("defendants")
  val defendants: List<Defendant> = emptyList(),

  @JsonProperty("caseStatus")
  val caseStatus: String?

)
