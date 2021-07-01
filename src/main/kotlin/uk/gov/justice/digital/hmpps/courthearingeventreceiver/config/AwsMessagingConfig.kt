package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class AwsMessagingConfig {

  @Bean
  fun amazonSNSClient(
    @Value("\${aws.region-name}") regionName: String,
    @Value("\${aws_sns_access_key_id}") awsAccessKeyId: String,
    @Value("\${aws_sns_secret_access_key}") awsSecretAccessKey: String
  ): AmazonSNS {
    return AmazonSNSClientBuilder
      .standard()
      .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey)))
      .withRegion(regionName)
      .build()
  }
}
