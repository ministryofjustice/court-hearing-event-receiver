package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.MessageAttributeValue
import com.amazonaws.services.sns.model.PublishRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType

private const val MESSAGE_TYPE = "COMMON_PLATFORM_HEARING"

@Component
class MessageNotifier(
  @Autowired
  private val objectMapper: ObjectMapper,
  @Autowired
  private val amazonSNSClient: AmazonSNS,
  @Value("\${aws.sns.topic_arn}")
  private val topicArn: String
) {
  fun send(hearingEventType: HearingEventType, hearingEvent: HearingEvent) {

    val messageTypeValue = MessageAttributeValue()
      .withDataType("String")
      .withStringValue(MESSAGE_TYPE)

    val hearingEventTypeValue = MessageAttributeValue()
      .withDataType("String")
      .withStringValue(hearingEventType.description)

    val publishRequest = PublishRequest(topicArn, objectMapper.writeValueAsString(hearingEvent))
      .withMessageAttributes(mapOf("messageType" to messageTypeValue, "hearingEventType" to hearingEventTypeValue))
    val publishResult = amazonSNSClient.publish(publishRequest)
    log.info("Published message with message Id {}", publishResult.messageId)
  }

  companion object {
    private val log = LoggerFactory.getLogger(MessageNotifier::class.java)
  }
}
