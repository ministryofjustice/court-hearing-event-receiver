package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.s3.S3AsyncClient

@Profile("!test")
@Configuration
class AwsConfig {
  @Bean
  fun awsS3AsyncClient(@Value("\${aws.region_name}") regionName: String): S3AsyncClient {
    return S3AsyncClient.builder().region(software.amazon.awssdk.regions.Region.of(regionName)).build()
  }
}
