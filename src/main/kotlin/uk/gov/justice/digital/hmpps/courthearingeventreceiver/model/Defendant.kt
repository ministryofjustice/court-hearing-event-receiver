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
    val id = node.get("id").stringValue("")
    val offences: List<Offence> =
      if (!node.has("offences") || node.get("offences").isNull) {
        emptyList()
      } else {
        node.get("offences").toList().map { context.readTreeAsValue(it, Offence::class.java) }
      }

    val prosecutionCaseId = node.get("prosecutionCaseId").stringValue()
    
    val personDefendant =
      node.optional("personDefendant").map { context.readTreeAsValue(it, PersonDefendant::class.java) }.orElse(null)
    val legalEntityDefendant =
      node.optional("legalEntityDefendant").map { context.readTreeAsValue(it, LegalEntityDefendant::class.java) }
        .orElse(null)
    val masterDefendantId = node.optional("masterDefendantId").map { it.asString() }.orElse(null)

    val isYouthMissing = node.optional("isYouthMissing").map { it.asBoolean() }.orElse(!node.has("isYouth"))
    val isYouth = node.optional("isYouth").map { it.asBoolean() }.orElse(null)
    val isPncMissing = node.optional("isPncMissing").map { it.asBoolean() }.orElse(!node.has("pncId"))
    val pncId = node.optional("pncId").map { it.asString() }.orElse(null)
    val isCroMissing = node.optional("isCroMissing").map { it.asBoolean() }.orElse(!node.has("croNumber"))
    val croNumber = node.optional("croNumber").map { it.asString() }.orElse(null)

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
