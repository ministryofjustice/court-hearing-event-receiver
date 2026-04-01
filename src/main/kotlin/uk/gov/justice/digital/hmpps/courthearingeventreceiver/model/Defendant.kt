package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.deser.std.StdDeserializer

@JsonDeserialize(using = DefendantDeserializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Defendant(

  @field:NotBlank
  @JsonProperty("id")
  val id: String,

  @field:Valid
  @field:NotEmpty
  @JsonProperty("offences")
  val offences: List<Offence> = emptyList(),

  // This looks to be simply the same value as the id in the parent ProsecutionCase.
  @field:NotBlank
  @JsonProperty("prosecutionCaseId")
  val prosecutionCaseId: String,

  // There will be a personDefendant OR legalEntityDefendant
  @field:Valid
  @JsonProperty("personDefendant")
  val personDefendant: PersonDefendant?,

  @field:Valid
  @JsonProperty("legalEntityDefendant")
  val legalEntityDefendant: LegalEntityDefendant?,

  @JsonProperty("masterDefendantId")
  val masterDefendantId: String?,

  @JsonProperty("pncId")
  val pncId: String?,

  @JsonProperty("croNumber")
  val croNumber: String?,

  @JsonProperty("isYouth")
  val isYouth: Boolean?,

  val isPncMissing: Boolean,
  val isCroMissing: Boolean,
  val isYouthMissing: Boolean,
)

class DefendantDeserializer : StdDeserializer<Defendant>(Defendant::class.java) {
  override fun deserialize(parser: JsonParser, context: DeserializationContext): Defendant {
    val node: JsonNode = parser.readValueAsTree()
    val id = node.get("id").textValue()
    val offences: List<Offence> =
      if (!node.has("offences") || node.get("offences").isNull) {
        emptyList()
      } else {
        node.get("offences").toList().map { context.readTreeAsValue(it, Offence::class.java) }
      }

    val prosecutionCaseId = node.get("prosecutionCaseId").textValue()

    val personDefendant = if (node.has("personDefendant") && !node.get("personDefendant").isNull) {
      context.readTreeAsValue(node.get("personDefendant"), PersonDefendant::class.java)
    } else {
      null
    }

    val legalEntityDefendant = if (node.has("legalEntityDefendant") && !node.get("legalEntityDefendant").isNull) {
      context.readTreeAsValue(node.get("legalEntityDefendant"), LegalEntityDefendant::class.java)
    } else {
      null
    }

    val masterDefendantId = if (node.has("masterDefendantId") && !node.get("masterDefendantId").isNull) {
      node.get("masterDefendantId").textValue()
    } else {
      null
    }

    val isYouthMissing = if (node.has("isYouthMissing")) {
      node.get("isYouthMissing").booleanValue()
    } else {
      !node.has("isYouth") || node.get("isYouth").isNull
    }

    val isYouth = if (node.has("isYouth") && !node.get("isYouth").isNull) {
      node.get("isYouth").booleanValue()
    } else {
      null
    }

    val isPncMissing = if (node.has("isPncMissing")) {
      node.get("isPncMissing").booleanValue()
    } else {
      !node.has("pncId") || node.get("pncId").isNull
    }

    val pncId = if (node.has("pncId") && !node.get("pncId").isNull) {
      node.get("pncId").textValue()
    } else {
      null
    }

    val isCroMissing = if (node.has("isCroMissing")) {
      node.get("isCroMissing").booleanValue()
    } else {
      !node.has("croNumber") || node.get("croNumber").isNull
    }

    val croNumber = if (node.has("croNumber") && !node.get("croNumber").isNull) {
      node.get("croNumber").textValue()
    } else {
      null
    }

    return Defendant(
      id,
      offences,
      prosecutionCaseId,
      personDefendant,
      legalEntityDefendant,
      masterDefendantId,
      pncId,
      croNumber,
      isYouth,
      isPncMissing,
      isCroMissing,
      isYouthMissing,
    )
  }
}
