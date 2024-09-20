package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MessageAttributeTest {

  @Test
  fun `when there is a result path return RESULT as type`() {
    assertThat(getMessageType("/hearing/$UUID/result")).isSameAs(MessageType.RESULT)
    assertThat(getMessageType("/hearing/$UUID/result/")).isSameAs(MessageType.RESULT)
  }

  @Test
  fun `when there is a result path return DELETE as type`() {
    assertThat(getMessageType("/hearing/$UUID/delete")).isSameAs(MessageType.DELETE)
    assertThat(getMessageType("/hearing/$UUID/delete/")).isSameAs(MessageType.DELETE)
  }

  @Test
  fun `when there is a confirm update path return CONFIRM_UPDATE as type`() {
    val messageType = getMessageType("/hearing/$UUID")

    assertThat(messageType).isSameAs(MessageType.CONFIRM_UPDATE)
  }

  @Test
  fun `when the path starts with hearing be lenient whatever happens after`() {
    val messageType = getMessageType("/hearing/$UUID/operation/to/be/defined")

    assertThat(messageType).isSameAs(MessageType.CONFIRM_UPDATE)
  }

  @Test
  fun `when there is some other unknown path then record it as unknown`() {
    val messageType = getMessageType("/someother/$UUID/operation/")

    assertThat(messageType).isSameAs(MessageType.UNKNOWN)
  }

  companion object {
    const val UUID = "c4bccab9-2261-427e-8c03-76c337b6593b"
  }
}
