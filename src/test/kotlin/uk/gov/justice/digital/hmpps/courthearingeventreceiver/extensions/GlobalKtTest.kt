package uk.gov.justice.digital.hmpps.courthearingeventreceiver.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.MessageType
import java.time.LocalDateTime
import java.time.Month

internal class GlobalKtTest {
  @Test
  fun `get path`() {
    val courtCode = "B10JQ"
    val messageType = MessageType.RESULT
    val receiptTime = LocalDateTime.of(2021, Month.DECEMBER, 25, 23, 59, 59, 123000)

    val path = buildS3Key(courtCode, messageType, receiptTime, UUID)

    assertThat(path).isEqualTo("cp/" + messageType.name + "/" + courtCode + "/2021-12-25/23-59-59-123000-" + UUID)
  }

  @Test
  fun `get UUID`() {
    val uuid = findUuid("/hearing/$UUID/delete")

    assertThat(uuid).isEqualTo(UUID)
  }

  @Test
  fun `get UUID when it is not present`() {
    val uuid = findUuid("/hearing/delete")

    assertThat(uuid).isEmpty()
  }

  @Test
  fun `given multiple UUIDs when get UUID then return the first`() {
    val uuid = findUuid("/hearing/$UUID/delete/8a400612-a942-41b9-ab04-8d090e20b095")

    assertThat(uuid).isEqualTo(UUID)
  }

  companion object {
    const val UUID = "c4bccab9-2261-427e-8c03-76c337b6593b"
  }
}
