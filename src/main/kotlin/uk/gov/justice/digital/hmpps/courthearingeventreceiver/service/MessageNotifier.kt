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

private const val MESSAGE_TYPE = "CP_TEST_COURT_CASE"

@Component
class MessageNotifier(
  @Autowired
  private val objectMapper: ObjectMapper,
  @Autowired
  private val amazonSNSClient: AmazonSNS,
  @Value("\${aws.sns.topic_arn}")
  private val topicArn: String
) {
  fun send(hearingEvent: HearingEvent) {

    val messageValue = MessageAttributeValue()
      .withDataType("String")
      .withStringValue(MESSAGE_TYPE)

    val publishRequest = PublishRequest(topicArn, objectMapper.writeValueAsString(hearingEvent))
      .withMessageAttributes(mapOf("messageType" to messageValue))
    val publishResult = amazonSNSClient.publish(publishRequest)
    log.info("Published message with message Id {}", publishResult.messageId)
  }

  companion object {
    private val log = LoggerFactory.getLogger(MessageNotifier::class.java)
  }
}
