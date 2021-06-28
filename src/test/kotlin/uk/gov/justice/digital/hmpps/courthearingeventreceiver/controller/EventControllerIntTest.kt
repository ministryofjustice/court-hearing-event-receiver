package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase

class EventControllerIntTest : IntegrationTestBase() {

  @Test
  fun whenPostToEventEndpointWithRequiredRole_thenReturn204NoContent() {

    postEvent(
      "foo",
      jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE"))
    )
      .exchange()
      .expectStatus().isAccepted
  }

  @Test
  fun whenPostToEventEndpointWithoutRequiredRole_thenReturn403Forbidden() {

    postEvent("foo", jwtHelper.createJwt("common-platform-events"))
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun whenPostToEventEndpointWithBadToken_thenReturn401Unauthorized() {
    val path = "/event"
    val token = "bad_token"
    postEvent(path, token)
      .exchange()
      .expectStatus().isUnauthorized
  }

  private fun postEvent(body: String, token: String) =
    webTestClient
      .post()
      .uri("/event")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer $token")
      .body(Mono.just(body), String.javaClass)
}
