package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class AwsConfig {

  @Bean
  fun amazonS3Client(
    @Value("\${aws.region-name}") regionName: String,
    @Value("\${aws.s3.access_key_id}") s3AccessKeyId: String,
    @Value("\${aws.s3.secret_access_key}") s3SecretAccessKey: String
  ): AmazonS3 {
    val credentials: AWSCredentials = BasicAWSCredentials(s3AccessKeyId, s3SecretAccessKey)

    return AmazonS3ClientBuilder
      .standard()
      .withCredentials(AWSStaticCredentialsProvider(credentials))
      .withRegion(regionName)
      .build()
  }
}
