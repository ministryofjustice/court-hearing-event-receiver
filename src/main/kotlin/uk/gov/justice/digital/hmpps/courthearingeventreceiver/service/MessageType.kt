package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import org.slf4j.LoggerFactory

enum class MessageType {
  CONFIRM_UPDATE,
  RESULT,
  DELETE,
  UNKNOWN,
}

private val log = LoggerFactory.getLogger(MessageType::class.java)

fun getMessageType(path: String): MessageType {
  val elements = path.lowercase().trim('/').split("/")

  return when (elements.last()) {
    "delete" -> MessageType.DELETE
    "result" -> MessageType.RESULT
    else -> {
      when (path.contains("hearing")) {
        true -> MessageType.CONFIRM_UPDATE
        false -> {
          log.error("Unable to correctly set the path from $path")
          MessageType.UNKNOWN
        }
      }
    }
  }
}
