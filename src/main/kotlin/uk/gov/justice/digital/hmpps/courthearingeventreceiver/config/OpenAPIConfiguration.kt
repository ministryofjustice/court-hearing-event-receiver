package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
@OpenAPIDefinition(
  info = io.swagger.v3.oas.annotations.info.Info(
    title = "Court Hearing Event Receiver API Documentation",
    description = "API to receive court hearing events from Common Platform and publish those events to an SNS topic.",
    contact = io.swagger.v3.oas.annotations.info.Contact(
      name = "Probation In Court Team",
      email = "",
      url = "https://moj.enterprise.slack.com/archives/C01FR4HKS3A",
    ),
    license = License(name = "The MIT License (MIT)", url = "https://github.com/ministryofjustice/court-hearing-event-receiver/blob/main/LICENSE"),
    version = "1.0",
  ),
  security = [SecurityRequirement(name = "hmpps-auth-token")],
)
@SecurityScheme(
  name = "hmpps-auth-token",
  scheme = "bearer",
  bearerFormat = "JWT",
  description = "OAuth2 bearer token requires ROLE_COURT_HEARING_EVENT_WRITE",
  type = SecuritySchemeType.HTTP,
  `in` = SecuritySchemeIn.HEADER,
  paramName = HttpHeaders.AUTHORIZATION,
)
class OpenAPIConfiguration
