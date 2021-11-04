package uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions

import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun buildS3Key(courtCode: String, messageType: MessageType, receiptTime: LocalDateTime, hearingEventId: String): String {
  return "cp".plus(PATH_DELIMITER).plus(messageType)
    .plus(PATH_DELIMITER).plus(courtCode)
    .plus(PATH_DELIMITER).plus(receiptTime.toLocalDate().toString())
    .plus(PATH_DELIMITER).plus(receiptTime.toLocalTime().format(formatter))
    .plus(FIELD_DELIMITER).plus(hearingEventId)
}

fun findUuid(string: String): String = UUID_REGEX.find(string).let { it?.value ?: "" }

@JvmField
var PATH_DELIMITER = "/"
var FIELD_DELIMITER = "-"

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss")

@JvmField
var UUID_REGEX = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}".toRegex()
