package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions.buildS3Key
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions.findUuid
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import java.lang.RuntimeException
import java.time.LocalDateTime

@Component
class S3Service(
  @Value("\${aws.s3.bucket_name}") private val bucketName: String,
  @Autowired private val amazonS3Client: AmazonS3,
  @Autowired private val mapper: ObjectMapper,
) {
  fun uploadMessage(uriPath: String, messageContent: String): String? {
    val s3Key = buildS3Key(
      courtCode = getCourtCode(messageContent),
      receiptTime = LocalDateTime.now(),
      messageType = getMessageType(uriPath),
      hearingEventId = findUuid(uriPath),
    )

    return try {
      val putResult = amazonS3Client.putObject(bucketName, s3Key, messageContent)
      log.info("File {} saved to S3 bucket {} with expiration date of {}, eTag {}", s3Key, bucketName, putResult.expirationTime, putResult.eTag)
      putResult.eTag
    } catch (ex: RuntimeException) {
      // Happy to swallow this one with a log statement because failure to back up the file is not business critical
      log.error("Failed to back up file {} to S3 bucket {}", s3Key, bucketName, ex)
      null
    }
  }

  fun getCourtCode(messageContent: String): String {
    return try {
      val hearingEvent = mapper.readValue<HearingEvent>(messageContent)
      hearingEvent.hearing.courtCentre.code
    } catch (ex: JsonProcessingException) {
      log.error("Failed to parse ", ex)
      "UNKNOWN_COURT"
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(S3Service::class.java)
  }
}
