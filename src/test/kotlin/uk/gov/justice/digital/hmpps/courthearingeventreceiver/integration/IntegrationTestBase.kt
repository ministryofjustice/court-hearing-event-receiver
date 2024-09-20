package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
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
  class AwsTestConfig() {

    @Bean
    fun amazonS3Client(
      @Value("\${aws.region-name}") regionName: String,
      @Value("\${aws.s3.access_key_id}") s3AccessKeyId: String,
      @Value("\${aws.s3.secret_access_key}") s3SecretAccessKey: String,
    ): AmazonS3 {
      val credentials: AWSCredentials = BasicAWSCredentials(s3AccessKeyId, s3SecretAccessKey)

      return AmazonS3ClientBuilder
        .standard()
        .withCredentials(AWSStaticCredentialsProvider(credentials))
        .withRegion(regionName)
        .build()
    }
  }
}
