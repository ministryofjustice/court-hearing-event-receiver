package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.License
import org.springframework.context.annotation.Configuration

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
    license = License(name = "The MIT License (MIT)", url = "https://github.com/ministryofjustice/court-case-service/blob/main/LICENSE"),
    version = "1.0",
  ),
)
class OpenAPIConfiguration
