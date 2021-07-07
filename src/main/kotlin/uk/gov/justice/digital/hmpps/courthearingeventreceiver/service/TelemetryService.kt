package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TelemetryService(@Autowired private val telemetryClient: TelemetryClient) {

  fun trackEvent(eventType: TelemetryEventType, customDimensions: Map<String, String?>) {
    telemetryClient.trackEvent(eventType.eventName, customDimensions, null)
  }
}
