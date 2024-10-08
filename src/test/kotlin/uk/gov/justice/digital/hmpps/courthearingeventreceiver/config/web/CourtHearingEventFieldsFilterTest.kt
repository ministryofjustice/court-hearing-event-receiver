package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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

@ExtendWith(MockitoExtension::class)
internal class CourtHearingEventFieldsFilterTest {

  @Mock
  private lateinit var telemetryService: TelemetryService

  @Mock
  private lateinit var request: HttpServletRequest

  @Mock
  private lateinit var response: HttpServletResponse

  @Mock
  private lateinit var inputStream: ServletInputStream

  @Mock
  private lateinit var chain: FilterChain

  private lateinit var observeFields: ObserveFields

  private lateinit var defenceOrgFieldDetails: ObserveFieldDetails

  private lateinit var pncFieldDetails: ObserveFieldDetails

  private lateinit var croFieldDetails: ObserveFieldDetails

  private lateinit var observeFieldsMap: Map<String, ObserveFieldDetails>

  private lateinit var courtHearingEventFieldsFilter: CourtHearingEventFieldsFilter

  private lateinit var hearingTypeFieldDetails: ObserveFieldDetails

  @BeforeEach
  fun setUp() {
    defenceOrgFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].defenceOrganisation[*]", false)
    pncFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].pncId", false)
    croFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].croNumber", false)

    observeFieldsMap = buildMap {
      put("defenceOrganisation", defenceOrgFieldDetails)
      put("pncId", pncFieldDetails)
      put("croNumber", croFieldDetails)
    }
    observeFields = ObserveFields(observeFieldsMap)
  }

  @Test
  fun `when request method is a POST and no observe fields configured then do not send any details`() {
    whenever(request.method).thenReturn("POST")
    observeFields = ObserveFields(emptyMap())
    courtHearingEventFieldsFilter = CourtHearingEventFieldsFilter(telemetryService, observeFields)

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    verify(chain).doFilter(same(request), same(response))
    verifyNoInteractions(telemetryService)
  }

  @Test
  fun `when request method is a GET then do not send any details`() {
    whenever(request.method).thenReturn("GET")
    courtHearingEventFieldsFilter = CourtHearingEventFieldsFilter(telemetryService, observeFields)

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    verify(chain).doFilter(same(request), same(response))
    verifyNoInteractions(telemetryService)
  }

  @Test
  fun `when print value is false for the observe fields then send event with fields exist as true`() {
    whenever(request.method).thenReturn("POST")
    whenever(request.inputStream).thenReturn(inputStream)
    val byteArray = HEARING_EVENT_JSON.toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)
    whenever(request.characterEncoding).thenReturn(CHARSET.name())

    courtHearingEventFieldsFilter = CourtHearingEventFieldsFilter(telemetryService, observeFields)

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    val fieldsNotPresentMap = buildMap {
      put("defenceOrganisation", "true")
      put("pncId", "true")
      put("croNumber", "true")
    }

    verify(chain).doFilter(isA<CustomHttpRequestWrapper>(), same(response))
    verify(telemetryService).trackEvent(TelemetryEventType.COMMON_PLATFORM_EVENT_OBSERVED, fieldsNotPresentMap)
  }

  @Test
  fun `when print value is true for the observe fields then send event with field details`() {
    defenceOrgFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].defenceOrganisation[*]", true)
    pncFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].pncId", true)
    croFieldDetails = ObserveFieldDetails("hearing.prosecutionCases[*].defendants[*].croNumber", true)
    hearingTypeFieldDetails = ObserveFieldDetails("hearing.type.description", true)

    observeFieldsMap = buildMap {
      put("defenceOrganisation", defenceOrgFieldDetails)
      put("pncId", pncFieldDetails)
      put("croNumber", croFieldDetails)
      put("hearingType", hearingTypeFieldDetails)
    }
    observeFields = ObserveFields(observeFieldsMap)

    whenever(request.method).thenReturn("POST")
    whenever(request.inputStream).thenReturn(inputStream)
    val byteArray = HEARING_EVENT_JSON.toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)
    whenever(request.characterEncoding).thenReturn(CHARSET.name())

    courtHearingEventFieldsFilter = CourtHearingEventFieldsFilter(telemetryService, observeFields)

    courtHearingEventFieldsFilter.doFilter(request, response, chain)

    val fieldsPresentMap = buildMap {
      put("defenceOrganisation", "[{\"name\":\"RAF\"},{\"name\":\"Royal Navy\",\"contact\":{\"primaryEmail\":\"test@test.com\"}}]")
      put("pncId", "[\"pnc number1\",\"pnc number2\"]")
      put("croNumber", "[\"cro number1\",\"cro number2\"]")
      put("hearingType", "[\"Sentence\"]")
    }

    verify(chain).doFilter(isA<CustomHttpRequestWrapper>(), same(response))
    verify(telemetryService).trackEvent(TelemetryEventType.COMMON_PLATFORM_EVENT_OBSERVED, fieldsPresentMap)
  }

  companion object {
    val HEARING_EVENT_JSON = File("src/test/resources/json/court-hearing-event.json").readText(Charsets.UTF_8)
    private val CHARSET = Charsets.UTF_8
  }
}
