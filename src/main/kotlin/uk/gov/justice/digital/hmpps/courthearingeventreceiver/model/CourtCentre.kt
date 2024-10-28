package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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

  @JsonProperty("name")
  @field:NotBlank
  val name: String,

  @JsonProperty("roomName")
  @field:NotBlank
  val roomName: String,

  @field:Valid
  @JsonProperty("address")
  val address: Address?,

  @JsonProperty("lja")
  val lja: LJA,
)

