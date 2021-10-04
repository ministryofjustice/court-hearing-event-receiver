package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions.UUID_REGEX
import java.lang.IllegalArgumentException

enum class MessageType() {
  CONFIRM_UPDATE(), RESULT(), DELETE(), UNKNOWN();
}

fun getMessageType(path: String): MessageType {

  val elements = path.lowercase().split("/")
  val finalElement = elements.last()

  return when (UUID_REGEX.matches(finalElement)) {
    true -> MessageType.CONFIRM_UPDATE
    else -> {
      try {
        MessageType.valueOf(finalElement.uppercase())
      } catch (iae: IllegalArgumentException) {
        MessageType.UNKNOWN
      }
    }
  }
}
