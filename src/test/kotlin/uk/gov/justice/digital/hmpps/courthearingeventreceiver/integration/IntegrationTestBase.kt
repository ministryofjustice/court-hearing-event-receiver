package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration

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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.JwtAuthenticationHelper
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import java.net.URI

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

  @Value("\${aws.s3.large_cases.bucket_name}")
  lateinit var largeCasesBucketName: String

  @Autowired
  lateinit var amazonS3: S3AsyncClient

  @Autowired
  lateinit var hmppsQueueService: HmppsQueueService
  val courtCasesQueue by lazy {
    hmppsQueueService.findByQueueId("courtcasesqueue")
  }

  @BeforeEach
  fun beforeEach() {
    courtCasesQueue!!.sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(courtCasesQueue!!.queueUrl).build())
    courtCasesQueue!!.sqsDlqClient?.purgeQueue(PurgeQueueRequest.builder().queueUrl(courtCasesQueue!!.dlqUrl).build())
    amazonS3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build())
    amazonS3.createBucket(CreateBucketRequest.builder().bucket(largeCasesBucketName).build())
  }

  @TestConfiguration
  class AwsTestConfig(

    @Value("\${aws.region-name}")
    var regionName: String,
    @Value("\${aws.localstack-endpoint-url}")
    var endpointUrl: String,
  ) {
    @Bean
    fun awsS3LocalStackAsyncClient(): S3AsyncClient = S3AsyncClient.builder().endpointOverride(URI(endpointUrl))
      .forcePathStyle(true).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("any", "any")))
      .region(software.amazon.awssdk.regions.Region.of(regionName)).build()
  }
}
