package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import io.sentry.Sentry
import io.sentry.spring.boot.jakarta.SentryProperties
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Manual Sentry configuration for Spring Boot 4 compatibility.
 * We exclude SentryAutoConfiguration to avoid WebClient integration issues,
 * but manually initialize Sentry to keep error tracking and other features.
 */
@Configuration
@EnableConfigurationProperties(SentryProperties::class)
@ConditionalOnProperty(name = ["sentry.dsn"])
class SentryConfiguration(
  private val sentryProperties: SentryProperties,
) {

  @PostConstruct
  fun initializeSentry() {
    if (!sentryProperties.dsn.isNullOrBlank()) {
      Sentry.init { options ->
        options.dsn = sentryProperties.dsn
        options.environment = sentryProperties.environment
        // Note: WebClient auto-instrumentation is not available due to Spring Boot 4 compatibility
        // but all other Sentry features (error tracking, manual spans, etc.) work normally
      }
    }
  }
}
