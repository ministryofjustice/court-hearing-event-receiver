package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.JwtAuthenticationHelper
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestBase.AwsTestConfig::class)
abstract class IntegrationTestBase {

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jwtHelper: JwtAuthenticationHelper

  @Value("\${aws.s3.bucket_name}")
  lateinit var bucketName: String

  @Autowired
  lateinit var hmppsQueueService: HmppsQueueService
  val courtCaseEventsQueue by lazy {
    hmppsQueueService.findByQueueId("courtcaseeventsqueue")
  }

  @BeforeEach
  fun beforeEach() {
    courtCaseEventsQueue!!.sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(courtCaseEventsQueue!!.queueUrl).build())
    courtCaseEventsQueue!!.sqsDlqClient?.purgeQueue(PurgeQueueRequest.builder().queueUrl(courtCaseEventsQueue!!.dlqUrl).build())
  }

  @TestConfiguration
  class AwsTestConfig(

    @Value("\${aws.region-name}")
    var regionName: String,
    @Value("\${aws.localstack_endpoint_url}")
    var endpointUrl: String,
  ) {

    @Bean
    fun amazonS3LocalStackClient(): AmazonS3 {
      val endpointConfiguration = AwsClientBuilder.EndpointConfiguration(endpointUrl, regionName)

      return AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpointConfiguration)
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("any", "any")))
        .build()
    }
  }
}
