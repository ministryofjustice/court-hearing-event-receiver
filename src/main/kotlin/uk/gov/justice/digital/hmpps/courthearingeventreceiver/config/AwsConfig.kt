package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

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
  fun amazonS3Client(@Value("\${aws.region-name}") regionName: String): AmazonS3 {
    return AmazonS3ClientBuilder
      .standard()
      .withRegion(regionName)
      .build()
  }
}
