package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase

class PingControllerIntTest : IntegrationTestBase() {

  @Test
  fun whenPing_thenPong() {
    // The BodyContentSpec has built-in functions for looking at JSON and XML but not sure for strings
    val str = String(
      bytes = webTestClient.get().uri("/ping")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBodyContent,
    )
    assertThat(str).isEqualTo("pong")
  }
}
