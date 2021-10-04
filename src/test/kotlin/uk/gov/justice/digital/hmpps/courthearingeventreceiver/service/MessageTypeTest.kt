package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MessageTypeTest {

  @Test
  fun `when there is a result path return RESULT as type`() {

    val messageType = getMessageType("/hearing/$UUID/result")

    assertThat(messageType).isSameAs(MessageType.RESULT)
  }

  @Test
  fun `when there is a result path return DELETE as type`() {

    val messageType = getMessageType("/hearing/$UUID/delete")

    assertThat(messageType).isSameAs(MessageType.DELETE)
  }

  @Test
  fun `when there is a confirm update path return CONFIRM_UPDATE as type`() {

    val messageType = getMessageType("/hearing/$UUID")

    assertThat(messageType).isSameAs(MessageType.CONFIRM_UPDATE)
  }

  @Test
  fun `when there is some other unknown path return UNKNOWN as type`() {

    val messageType = getMessageType("/hearing/$UUID/operation/to/be/defined")

    assertThat(messageType).isSameAs(MessageType.UNKNOWN)
  }

  companion object {
    const val UUID = "c4bccab9-2261-427e-8c03-76c337b6593b"
  }
}
