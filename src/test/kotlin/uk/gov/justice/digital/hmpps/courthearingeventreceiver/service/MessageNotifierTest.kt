package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.CourtCentre
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.Hearing
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.HearingEvent
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.HearingType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type.JurisdictionType

@ExtendWith(SpringExtension::class, MockitoExtension::class)
@Import(MessageNotifierTest.TestNotifierConfig::class)
internal class MessageNotifierTest {

  @Autowired
  private lateinit var messageNotifier: MessageNotifier

  @Autowired
  private lateinit var amazonSNSClient: AmazonSNS

  @Captor
  private lateinit var publishRequest: ArgumentCaptor<PublishRequest>

  @Test
  fun `when get message then publish to SNS`() {

    val hearingEvent = HearingEvent(
      hearing = Hearing(
        id = "hearing-id",
        courtCentre = CourtCentre(code = "B10JQ", roomId = "abc", roomName = "Crown Court 3-1", id = "abc"),
        type = HearingType(id = "abc", description = "Sentence"),
        jurisdictionType = JurisdictionType.MAGISTRATES
      )
    )
    val result = PublishResult().withMessageId("messageId")
    whenever(amazonSNSClient.publish(any()))
      .thenReturn(result)

    messageNotifier.send(hearingEvent)

    verify(amazonSNSClient).publish(publishRequest.capture())
    assertThat(publishRequest.value.message).contains("hearing-id")
    assertThat(publishRequest.value.messageAttributes["messageType"]?.dataType).isEqualTo("String")
    assertThat(publishRequest.value.messageAttributes["messageType"]?.stringValue).isEqualTo("CP_TEST_COURT_CASE")
  }

  @TestConfiguration
  class TestNotifierConfig {

    @MockBean
    private lateinit var amazonSNSClient: AmazonSNS

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    @Bean
    fun messageNotifier() = MessageNotifier(objectMapper(), amazonSNSClient, "topic")
  }
}
