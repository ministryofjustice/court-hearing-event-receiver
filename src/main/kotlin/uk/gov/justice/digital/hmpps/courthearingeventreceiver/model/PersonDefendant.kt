package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonDefendant(

  @NotNull
  @JsonProperty("personDetails")
  val personDetails: PersonDetails
)
