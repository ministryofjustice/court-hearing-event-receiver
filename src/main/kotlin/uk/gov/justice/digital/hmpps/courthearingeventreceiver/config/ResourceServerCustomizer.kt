package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import uk.gov.justice.hmpps.kotlin.auth.dsl.ResourceServerConfigurationCustomizer

@Configuration(proxyBeanMethods = false)
@Profile("!unsecured")
class ResourceServerCustomizer {
  @Bean
  fun hearingEventAuthorization(): ResourceServerConfigurationCustomizer = ResourceServerConfigurationCustomizer {
    unauthorizedRequestPaths {
      includeDefaults = false
      addPaths = setOf(
        "/health/**",
        "/info",
        "/health",
        "/ping",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/webjars/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/swagger-ui.html",
      )
    }
    anyRequestRole { defaultRole = "COURT_HEARING_EVENT_WRITE" }
  }

  @Bean
  fun statelessSessions(): Customizer<SessionManagementConfigurer<HttpSecurity>> = Customizer {
    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  }
}
