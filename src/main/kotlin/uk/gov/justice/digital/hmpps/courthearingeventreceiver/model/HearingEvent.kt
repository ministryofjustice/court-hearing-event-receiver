package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import org.jetbrains.annotations.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class HearingEvent(
  @field:Valid
  @field:NotNull
  @JsonProperty("hearing")
  var hearing: Hearing,
)

data class HearingDeletedEvent(val hearingId: String)
