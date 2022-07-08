package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.isA
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.io.File
import javax.servlet.FilterChain
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
internal class CourtHearingEventFieldsFilterTest {

  private lateinit var courtHearingEventFieldsFilter: CourtHearingEventFieldsFilter

  @Mock
  private lateinit var telemetryService: TelemetryService

  @Mock
  private lateinit var observeFields: ObserveFields

  @Mock
  private lateinit var request: HttpServletRequest

  @Mock
  private lateinit var response: HttpServletResponse

  @Mock
  private lateinit var inputStream: ServletInputStream

  @Mock
  private lateinit var chain: FilterChain

  private lateinit var observeFieldsMap: Map<String, String>

  @BeforeEach
  fun setUp() {
    courtHearingEventFieldsFilter = CourtHearingEventFieldsFilter(telemetryService, observeFields)
    observeFieldsMap = buildMap {
      put("defenceOrganisation", "hearing.prosecutionCases[*].defendants[*].defenceOrganisation[*]")
      put("pnc", "hearing.prosecutionCases[*].defendants[*].pncId")
      put("cro", "hearing.prosecutionCases[*].defendants[*].croNumber")
    }
  }

  @Test
  fun `when no observe fields configured then do not send any details`() {
    whenever(observeFields.fields).thenReturn(emptyMap())

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    verify(chain).doFilter(same(request), same(response))
    verifyNoInteractions(telemetryService)
  }

  @Test
  fun `when values present for the observe fields then send event with details`() {
    whenever(request.inputStream).thenReturn(inputStream)
    val byteArray = HEARING_EVENT_JSON.toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)
    whenever(request.characterEncoding).thenReturn(CHARSET.name())
    whenever(observeFields.fields).thenReturn(observeFieldsMap)

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    val fieldsNotPresentMap = buildMap {
      put("defenceOrganisation", "'[name]', '[name, contact]'")
      put("pnc", "true")
      put("cro", "true")
    }

    verify(chain).doFilter(isA<CustomHttpRequestWrapper>(), same(response))
    verify(telemetryService).trackEvent(TelemetryEventType.COMMON_PLATFORM_EVENT_OBSERVED, fieldsNotPresentMap)
  }

  companion object {
    val HEARING_EVENT_JSON = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
    private val CHARSET = Charsets.UTF_8
  }
}
