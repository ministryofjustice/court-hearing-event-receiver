package uk.gov.justice.digital.hmpps.courthearingeventreceiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CourtHearingEventReceiver

fun main(args: Array<String>) {
  runApplication<CourtHearingEventReceiver>(*args)
}
