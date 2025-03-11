package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI

@Profile("local")
@Configuration
class LocalAwsConfig(
  @Value("\${aws.region-name}")
  var regionName: String,
) {
  @Value("\${aws.localstack-endpoint-url}")
  lateinit var endpointUrl: String

  @Bean
  fun awsS3LocalStackAsyncClient(): S3AsyncClient = S3AsyncClient.builder().endpointOverride(URI(endpointUrl))
    .forcePathStyle(true).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("any", "any")))
    .region(software.amazon.awssdk.regions.Region.of(regionName)).build()
}
