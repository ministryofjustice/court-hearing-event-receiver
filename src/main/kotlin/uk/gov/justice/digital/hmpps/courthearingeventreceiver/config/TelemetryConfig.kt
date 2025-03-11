package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.lang.NonNull

@Configuration
class TelemetryConfig {

  @Bean
  @Conditional(AppInsightKeyAbsentCondition::class)
  fun getTelemetryClient(): TelemetryClient = TelemetryClient()

  private class AppInsightKeyAbsentCondition : Condition {
    override fun matches(@NonNull context: ConditionContext, @NonNull metadata: AnnotatedTypeMetadata): Boolean = context.environment.getProperty("application.insights.ikey").isNullOrEmpty()
  }
}
