package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

@Configuration
@EnableWebSecurity
@Profile("!unsecured")
class ResourceServerConfiguration : WebSecurityConfigurerAdapter() {
  override fun configure(http: HttpSecurity) {
    http
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and().csrf().disable()
      .authorizeRequests {
        it.antMatchers(
          "/health/**",
          "/info",
          "/health",
          "/ping",
          "/swagger-resources/**",
          "/v2/api-docs",
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/webjars/springfox-swagger-ui/**"
        ).permitAll()
        it.anyRequest()
          .hasRole("COURT_HEARING_EVENT_WRITE")
      }
      .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
  }

  fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
    // hmpps auth tokens have roles in a custom `authorities` claim.
    // the authorities are already prefixed with `ROLE_`.
    val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities")
    grantedAuthoritiesConverter.setAuthorityPrefix("")

    val jwtAuthenticationConverter = JwtAuthenticationConverter()
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
    return jwtAuthenticationConverter
  }
}
