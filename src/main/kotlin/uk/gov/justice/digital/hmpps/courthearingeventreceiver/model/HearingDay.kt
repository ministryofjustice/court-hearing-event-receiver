package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@JsonIgnoreProperties(ignoreUnknown = true)
data class HearingDay(

  @NotNull
  @JsonProperty("sittingDay")
  val sittingDay: LocalDateTime,

  @Positive
  @JsonProperty("listedDurationMinutes")
  val listedDurationMinutes: Int,

  @JsonProperty("listingSequence")
  val listingSequence: Int?
)
