package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingEventType
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingTopicException
import uk.gov.justice.hmpps.sqs.publish

private const val MESSAGE_TYPE = "COMMON_PLATFORM_HEARING"

@Component
class MessageNotifier(
  private val objectMapper: ObjectMapper,
  private val hmppsQueueService: HmppsQueueService,
) {
  private val topic =
    hmppsQueueService.findByTopicId("courtcaseeventstopic")
      ?: throw MissingTopicException("Could not find topic ")
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

    val publishResult = topic.publish("commonplatform.case.received", objectMapper.writeValueAsString(hearingEvent), mapOf("messageType" to messageTypeValue, "hearingEventType" to hearingEventTypeValue))
    log.info("Published message with message Id {}", publishResult.messageId())
  }

  companion object {
    private val log = LoggerFactory.getLogger(MessageNotifier::class.java)
  }
}
