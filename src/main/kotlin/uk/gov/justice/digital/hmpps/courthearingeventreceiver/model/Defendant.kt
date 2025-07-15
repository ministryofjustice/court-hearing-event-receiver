package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

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
  override fun deserialize(parser: JsonParser, context: DeserializationContext): Defendant? {
    val node = parser.codec.readTree<JsonNode>(parser)
    val id = node.get("id").asText()
    val offences: List<Offence> =
      if (!node.has("offences") || node.get("offences").isNull) {
        emptyList()
      } else {
        (node.get("offences") as ArrayNode)
          .map { parser.codec.treeToValue(it, Offence::class.java) }
      }

    val prosecutionCaseId = node.get("prosecutionCaseId").asText()

    val personDefendant =
      node.optional("personDefendant").map { parser.codec.treeToValue(it, PersonDefendant::class.java) }.orElse(null)
    val legalEntityDefendant =
      node.optional("legalEntityDefendant").map { parser.codec.treeToValue(it, LegalEntityDefendant::class.java) }
        .orElse(null)
    val masterDefendantId = node.optional("masterDefendantId").map { it.asText() }.orElse(null)
    val isYouthMissing = node.optional("isYouthMissing").map { it.asBoolean() }.orElse(!node.has("isYouth"))
    val isYouth = node.optional("isYouth").map { it.asBoolean() }.orElse(null)
    val isPncMissing = node.optional("isPncMissing").map { it.asBoolean() }.orElse(!node.has("pncId"))
    val pncId = node.optional("pncId").map { it.asText() }.orElse(null)
    val isCroMissing = node.optional("isCroMissing").map { it.asBoolean() }.orElse(!node.has("croNumber"))
    val croNumber = node.optional("croNumber").map { it.asText() }.orElse(null)

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
