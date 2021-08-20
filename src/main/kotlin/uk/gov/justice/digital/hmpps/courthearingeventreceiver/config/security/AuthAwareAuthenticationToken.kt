package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class AuthAwareAuthenticationToken(jwt: Jwt, clientOnly: Boolean, extractAuthorities: Collection<GrantedAuthority>?) : JwtAuthenticationToken(jwt, extractAuthorities) {
  private var subject: String? = null
  private var clientOnly = false
}
