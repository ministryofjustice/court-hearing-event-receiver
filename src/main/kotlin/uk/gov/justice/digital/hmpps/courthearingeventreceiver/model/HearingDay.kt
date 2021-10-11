package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

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
  val listingSequence: Int
)
