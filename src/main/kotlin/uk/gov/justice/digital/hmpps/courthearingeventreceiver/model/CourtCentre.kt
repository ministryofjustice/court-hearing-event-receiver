package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class CourtCentre(
  @NotNull
  @JsonProperty("id")
  val id: String,

  @JsonProperty("code")
  val code: String?,
  @JsonProperty("roomId")
  val roomId: String?,
  @JsonProperty("roomName")
  val roomName: String?
)