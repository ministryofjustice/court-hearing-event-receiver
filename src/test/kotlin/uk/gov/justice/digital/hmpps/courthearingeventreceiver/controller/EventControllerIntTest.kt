package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase

class EventControllerIntTest : IntegrationTestBase() {

  @Test
  fun whenPostToEventEndpoint_thenReturn204NoContent() {
    webTestClient
        .post()
        .uri("/event")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just("foo"), String.javaClass)
        .exchange()
        .expectStatus().isAccepted
  }
}
