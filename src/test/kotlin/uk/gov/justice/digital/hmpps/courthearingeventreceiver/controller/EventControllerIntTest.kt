package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration.IntegrationTestBase

@ActiveProfiles("test")
@Import(EventControllerIntTest.AwsTestConfig::class)
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
    val messages = sqs.receiveMessage("http://localhost:4566/000000000000/test-queue")
    assertThat(messages.messages.size).isEqualTo(1)
    assertThat(messages.messages.get(0)).isEqualTo("foo")
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

  @TestConfiguration
  class AwsTestConfig(
    @Value("\${aws.sqs_endpoint_url}")
    private val sqsEndpointUrl: String,
    @Value("\${aws.access_key_id}")
    private val accessKeyId: String,
    @Value("\${aws.secret_access_key}")
    private val secretAccessKey: String,
    @Value("\${aws.region_name}")
    private val regionName: String,
    @Value("\${aws.sqs.queue_name}")
    private val queueName: String
  ) {

    @Primary
    @Bean
    fun amazonSQSAsync(): AmazonSQSAsync {
      return AmazonSQSAsyncClientBuilder
        .standard()
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKeyId, secretAccessKey)))
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(sqsEndpointUrl, regionName))
        .build()
    }

    @Bean
    fun amazonSNSClient(
      @Value("\${aws.region-name}") regionName: String,
      @Value("\${aws_sns_access_key_id}") awsAccessKeyId: String,
      @Value("\${aws_sns_secret_access_key}") awsSecretAccessKey: String
    ): AmazonSNS {
      return AmazonSNSClientBuilder
        .standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(sqsEndpointUrl, regionName))
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey)))
        .build()
    }
  }
}
