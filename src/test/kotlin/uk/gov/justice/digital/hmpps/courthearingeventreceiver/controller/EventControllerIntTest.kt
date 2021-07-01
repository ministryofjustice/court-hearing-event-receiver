package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.amazonaws.services.sqs.AmazonSQSAsync
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
class EventControllerIntTest : IntegrationTestBase() {
  @Autowired
  lateinit var sqs: AmazonSQSAsync

  @Test
  fun whenPostToEventEndpointWithRequiredRole_thenReturn204NoContent_andPushToTopic() {

    postEvent(
      "foo",
      jwtHelper.createJwt("common-platform-events", roles = listOf("ROLE_COURT_HEARING_EVENT_WRITE"))
    )
      .exchange()
      .expectStatus().isAccepted

    // Verify new thing received at topic
    val messages = sqs.receiveMessageAsync("http://localhost:4566/000000000000/test-queue")
      .get(5, TimeUnit.SECONDS)
    assertThat(messages.messages.size).isEqualTo(1)
    assertThat(messages.messages.get(0).body).contains("foo")
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
