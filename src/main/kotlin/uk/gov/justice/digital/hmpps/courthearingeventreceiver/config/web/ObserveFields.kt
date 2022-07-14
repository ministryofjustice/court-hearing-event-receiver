package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "observe")
data class ObserveFields(val fields: Map<String, ObserveFieldDetails>)
