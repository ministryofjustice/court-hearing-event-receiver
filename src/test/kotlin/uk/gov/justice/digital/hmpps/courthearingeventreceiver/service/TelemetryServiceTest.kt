package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.microsoft.applicationinsights.TelemetryClient
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TelemetryServiceTest {

  @Mock
  private lateinit var telemetryClient: TelemetryClient

  @InjectMocks
  lateinit var telemetryService: TelemetryService
  @Test
  fun `when track event then use customDimensions map`() {
    val map = mapOf("key" to "value")
    telemetryService.trackEvent(TelemetryEventType.COURT_HEARING_UPDATE_EVENT_RECEIVED, map)

    verify(telemetryClient).trackEvent("PiCCourtHearingUpdateEventReceived", map, null)
  }
}
