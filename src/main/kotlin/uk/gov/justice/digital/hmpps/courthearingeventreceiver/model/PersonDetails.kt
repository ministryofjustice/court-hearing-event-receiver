package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.Gender
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonDetails(

  @field:NotNull
  @JsonProperty("gender")
  val gender: Gender,

  @field:NotBlank
  @JsonProperty("lastName")
  val lastName: String,

  @JsonProperty("middleName")
  val middleName: String?,

  @JsonProperty("firstName")
  val firstName: String?,

  @JsonProperty("title")
  val title: String?,

  @JsonProperty("dateOfBirth")
  val dateOfBirth: LocalDate?,

  @field:Valid
  @JsonProperty("address")
  val address: Address?,

  @JsonProperty("contact")
  val contact: Contact?,

  val ethnicity: Ethnicity?,
)
