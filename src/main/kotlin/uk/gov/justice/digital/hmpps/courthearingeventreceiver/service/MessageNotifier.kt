package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.sns.AmazonSNSExtendedAsyncClient
import software.amazon.sns.SNSExtendedAsyncClientConfiguration
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingDeletedEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingTopicException
import uk.gov.justice.hmpps.sqs.publish

private const val MESSAGE_TYPE = "COMMON_PLATFORM_HEARING"
private const val MESSAGE_GROUP_ID = "COURT_HEARING_EVENT_RECEIVER"

@Component
class MessageNotifier(
  private val objectMapper: ObjectMapper,
  private val hmppsQueueService: HmppsQueueService,
  private val amazonS3AsyncClient: S3AsyncClient,
  @Value("\${aws.s3.large_cases.bucket_name}") private val bucketName: String,
) {

  private val topic =
    hmppsQueueService.findByTopicId("courtcasestopic")
      ?: throw MissingTopicException("Could not find topic ")

  private val maxMessageSize = 256 * 1024

  fun send(hearingEventType: HearingEventType, hearingEvent: HearingEvent) {
    val messageId = when {
      messageLargerThanThreshold(hearingEvent) -> publishLargeMessage(hearingEvent, hearingEventType)
      else -> publishMessage(hearingEvent, hearingEventType)
    }

    log.info("Published message with message Id {}", messageId)
  }

  fun send(hde: HearingDeletedEvent) {
    val messageTypeValue =
      MessageAttributeValue.builder()
        .dataType("String")
        .stringValue("COMMON_PLATFORM_HEARING_DELETED")
        .build()

    val hearingEventTypeValue =
      MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(HearingEventType.DELETED.description)
        .build()

    val messageId = topic.publish(
      "commonplatform.case.deleted",
      objectMapper.writeValueAsString(hde),
      attributes = mapOf("messageType" to messageTypeValue, "hearingEventType" to hearingEventTypeValue),
      messageGroupId = MESSAGE_GROUP_ID,
    ).messageId()

    log.info("Published deleted message with id {}", messageId)
  }

  private fun publishMessage(hearingEvent: HearingEvent, hearingEventType: HearingEventType): String? {
    val messageTypeValue =
      MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(MESSAGE_TYPE)
        .build()

    val hearingEventTypeValue =
      MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(hearingEventType.description)
        .build()

    val publishResult = topic.publish(eventType = "commonplatform.case.received", event = objectMapper.writeValueAsString(hearingEvent), attributes = mapOf("messageType" to messageTypeValue, "hearingEventType" to hearingEventTypeValue), messageGroupId = MESSAGE_GROUP_ID)

    return publishResult.messageId()
  }

  fun publishLargeMessage(hearingEvent: HearingEvent, hearingEventType: HearingEventType): String? {
    val snsExtendedAsyncClientConfiguration: SNSExtendedAsyncClientConfiguration = SNSExtendedAsyncClientConfiguration()
      .withPayloadSupportEnabled(amazonS3AsyncClient, bucketName)

    val snsExtendedClient = AmazonSNSExtendedAsyncClient(
      topic.snsClient,
      snsExtendedAsyncClientConfiguration,
    )
    // Publish message via SNS with storage in S3
    val publishResult = snsExtendedClient.publish(
      PublishRequest.builder().topicArn(topic.arn).messageAttributes(
        mapOf(
          "messageType" to MessageAttributeValue.builder().dataType("String").stringValue(MESSAGE_TYPE).build(),
          "hearingEventType" to MessageAttributeValue.builder().dataType("String").stringValue(hearingEventType.description).build(),
          "eventType" to MessageAttributeValue.builder().dataType("String").stringValue("commonplatform.large.case.received").build(),
        ),
      ).messageGroupId(MESSAGE_GROUP_ID).message(objectMapper.writeValueAsString(hearingEvent))
        .build(),
    )
    return publishResult.get().messageId()
  }

  fun messageLargerThanThreshold(hearingEvent: HearingEvent): Boolean = hearingEvent.toString().toByteArray().size >= maxMessageSize

  companion object {
    private val log = LoggerFactory.getLogger(MessageNotifier::class.java)
  }
}
