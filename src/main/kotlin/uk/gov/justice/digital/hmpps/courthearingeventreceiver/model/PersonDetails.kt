package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.Gender
import java.time.LocalDate
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonDetails(

  @NotNull
  @JsonProperty("gender")
  val gender: Gender,

  @NotBlank
  @JsonProperty("lastName")
  val lastName: String,

  @JsonProperty("firstName")
  val firstName: String,

  @JsonProperty("dateOfBirth")
  val dateOfBirth: LocalDate,

  @JsonProperty("address")
  val address: Address

)
