package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull
import javax.validation.Valid

@JsonIgnoreProperties(ignoreUnknown = true)
data class HearingEvent(
  @field:Valid
  @field:NotNull
  @JsonProperty("hearing")
  var hearing: Hearing
)
