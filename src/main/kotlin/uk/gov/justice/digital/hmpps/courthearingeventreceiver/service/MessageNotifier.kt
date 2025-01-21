package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.sns.AmazonSNSExtendedAsyncClient
import software.amazon.sns.SNSExtendedAsyncClientConfiguration
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
  @Autowired private val amazonS3AsyncClient: S3AsyncClient,
) {

  private val topic =
    hmppsQueueService.findByTopicId("courtcasestopic")
      ?: throw MissingTopicException("Could not find topic ")

  private val MAX_MESSAGE_SIZE = 256 * 1024;

  @Value("\${aws.s3.bucket_name}")
  private val bucket_name: String = ""

  fun send(hearingEventType: HearingEventType, hearingEvent: HearingEvent) {
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

    if (sizeOfMessage(hearingEvent) >= MAX_MESSAGE_SIZE) {
      publishLargeMessage(hearingEventType = hearingEventType, hearingEvent = hearingEvent)
      return
    }

    val publishResult = topic.publish(eventType = "commonplatform.case.received", event = objectMapper.writeValueAsString(hearingEvent), attributes = mapOf("messageType" to messageTypeValue, "hearingEventType" to hearingEventTypeValue), messageGroupId = MESSAGE_GROUP_ID)

    log.info("Published message with message Id {}", publishResult.messageId())
  }

  fun publishLargeMessage(hearingEventType: HearingEventType, hearingEvent: HearingEvent) {
    val hearingEventTypeValue =
      MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(hearingEventType.description)
        .build()

    // hmpps.topic library returns SNSAsyncClient which requires S3AsyncClient
    val snsExtendedAsyncClientConfiguration: SNSExtendedAsyncClientConfiguration = SNSExtendedAsyncClientConfiguration()
      .withPayloadSupportEnabled(amazonS3AsyncClient, bucket_name)

    val snsExtendedClient = AmazonSNSExtendedAsyncClient(
      topic.snsClient,
      snsExtendedAsyncClientConfiguration,
    )
    // Publish message via SNS with storage in S3
    val publishResult =  snsExtendedClient.publish(
      PublishRequest.builder().topicArn(topic.arn).messageAttributes(
        mapOf(
          "messageType" to MessageAttributeValue.builder().dataType("String").stringValue(MESSAGE_TYPE).build(),
          "hearingEventType" to hearingEventTypeValue,
          "eventType" to MessageAttributeValue.builder().dataType("String").stringValue("commonplatform.case.received").build()
        ),
      ).messageGroupId(MESSAGE_GROUP_ID).message(objectMapper.writeValueAsString(hearingEvent))
        .build(),
    )
    log.info("Published large message with message Id {}", publishResult.get().messageId())
  }

  fun sizeOfMessage(hearingEvent: HearingEvent): Int {
    return hearingEvent.toString().toByteArray().size
  }

  companion object {
    private val log = LoggerFactory.getLogger(MessageNotifier::class.java)
  }
}
