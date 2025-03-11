package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.security.AuthAwareTokenConverter

@Configuration
@EnableWebSecurity
@Profile("!unsecured")
class ResourceServerConfiguration {
  @Bean
  fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .csrf {
        it.disable()
      }
      .authorizeHttpRequests {
        it.requestMatchers(
          "/health/**",
          "/info",
          "/health",
          "/ping",
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/v3/swagger-ui.html",
        ).permitAll()
        it.anyRequest()
          .hasRole("COURT_HEARING_EVENT_WRITE")
      }
      .oauth2ResourceServer {
        it.jwt { config -> config.jwtAuthenticationConverter(AuthAwareTokenConverter()) }
      }
    return http.build()
  }
}
