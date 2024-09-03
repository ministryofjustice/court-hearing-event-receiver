package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class AwsConfig {

  @Bean
  fun amazonS3Client(@Value("\${aws.region-name}") regionName: String): AmazonS3 {
    return AmazonS3ClientBuilder
      .standard()
      .withRegion(regionName)
      .build()
  }

  @Bean
  fun amazonSNSClient(@Value("\${aws.region-name}") regionName: String): AmazonSNS {
    return AmazonSNSClientBuilder
      .standard()
      .withRegion(regionName)
      .build()
  }
}
