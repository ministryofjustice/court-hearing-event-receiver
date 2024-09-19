package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class HearingDay(

  @field:NotNull
  @JsonProperty("sittingDay")
  val sittingDay: LocalDateTime,

  @field:PositiveOrZero
  @JsonProperty("listedDurationMinutes")
  val listedDurationMinutes: Int,

  @field:PositiveOrZero
  @JsonProperty("listingSequence")
  val listingSequence: Int,
)
