package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.health

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InfoTestIntTest : IntegrationTestBase() {

  @Test
  fun `Info page reports version`() {
    webTestClient.get().uri("/info")
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("build.version").value<String> {
        assertThat(it).startsWith(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
      }
  }
}
