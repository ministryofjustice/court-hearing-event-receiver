package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@JsonIgnoreProperties(ignoreUnknown = true)
data class CourtCentre(
  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:NotBlank
  @field:Size(min = 5)
  @JsonProperty("code")
  val code: String,

  @field:NotBlank
  @JsonProperty("roomId")
  val roomId: String,

  @JsonProperty("roomName")
  @field:NotBlank
  val roomName: String
)
