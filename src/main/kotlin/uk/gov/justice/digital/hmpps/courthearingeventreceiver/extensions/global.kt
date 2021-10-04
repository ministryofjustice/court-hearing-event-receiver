package uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions

import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun buildS3Key(courtCode: String, messageType: MessageType, receiptTime: LocalDateTime, hearingEventId: String): String {
  return "cp" + PATH_DELIMITER + messageType + PATH_DELIMITER + courtCode + PATH_DELIMITER + receiptTime.toLocalDate().toString() + PATH_DELIMITER + receiptTime.toLocalTime().format(formatter) + FIELD_DELIMITER + hearingEventId
}

fun findUuid(string: String): String = UUID_REGEX.find(string).let { it?.value ?: "" }

@JvmField
var PATH_DELIMITER = "/"
var FIELD_DELIMITER = "-"

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss")

@JvmField
var UUID_REGEX = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}".toRegex()
