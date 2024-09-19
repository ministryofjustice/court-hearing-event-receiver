package uk.gov.justice.digital.hmpps.courthearingeventreceiver.integration

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.JwtAuthenticationHelper

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestBase.AwsTestConfig::class)
abstract class IntegrationTestBase {

  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jwtHelper: JwtAuthenticationHelper

  @Value("\${aws.s3.bucket_name}")
  lateinit var bucketName: String

  @TestConfiguration
  class AwsTestConfig(
    @Value("\${aws.sqs.endpoint_url}")
    private val sqsEndpointUrl: String,
    @Value("\${aws.sqs.access_key_id}")
    private val accessKeyId: String,
    @Value("\${aws.sqs.secret_access_key}")
    private val secretAccessKey: String,
    @Value("\${aws.region_name}")
    private val regionName: String,
    @Value("\${aws.sqs.queue_name}")
    private val queueName: String,
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
      @Value("\${aws.sns.access_key_id}") awsAccessKeyId: String,
      @Value("\${aws.sns.secret_access_key}") awsSecretAccessKey: String,
    ): AmazonSNS {
      return AmazonSNSClientBuilder
        .standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(sqsEndpointUrl, regionName))
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey)))
        .build()
    }

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
