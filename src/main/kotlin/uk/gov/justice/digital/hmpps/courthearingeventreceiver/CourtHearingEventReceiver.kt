package uk.gov.justice.digital.hmpps.courthearingeventreceiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CourtHearingEventReceiver

fun main(args: Array<String>) {
  runApplication<CourtHearingEventReceiver>(*args)
}
