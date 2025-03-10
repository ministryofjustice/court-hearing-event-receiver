package uk.gov.justice.digital.hmpps.courthearingeventreceiver.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

  @CrossOrigin
  @RequestMapping(value = ["/ping"], method = [RequestMethod.GET], produces = ["text/plain"])
  fun ping(): String = "pong"
}
