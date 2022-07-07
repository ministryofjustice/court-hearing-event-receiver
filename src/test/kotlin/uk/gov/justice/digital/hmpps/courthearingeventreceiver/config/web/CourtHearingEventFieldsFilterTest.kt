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
    private val CHARSET = Charsets.UTF_8
    private const val HEARING_EVENT_JSON = " {\n" +
      "  \"hearing\": {\n" +
      "    \"courtCentre\": {\n" +
      "      \"address\": {\n" +
      "        \"address1\": \"The Law Courts\",\n" +
      "        \"address2\": \"Alexandra Road\",\n" +
      "        \"address3\": \"Wimbledon\",\n" +
      "        \"address4\": \"\",\n" +
      "        \"address5\": \"\",\n" +
      "        \"postcode\": \"SW19 7JP\"\n" +
      "      },\n" +
      "      \"code\": \"B10JQ00\",\n" +
      "      \"name\": \"Wimbledon Magistrates' Court\",\n" +
      "      \"roomId\": \"1414ea28-8b0e-3ba7-8f97-f2bb6d5dd38c\",\n" +
      "      \"roomName\": \"Courtroom 05\",\n" +
      "      \"id\": \"59cb14a6-e8de-4615-9b9e-94bf5ef81ad2\"\n" +
      "    },\n" +
      "    \"hearingDays\": [\n" +
      "      {\n" +
      "        \"listedDurationMinutes\": 1,\n" +
      "        \"listingSequence\": 0,\n" +
      "        \"sittingDay\": \"2021-06-25T09:00:00.000Z\"\n" +
      "      }\n" +
      "    ],\n" +
      "    \"id\": \"59cb14a6-e8de-4615-9c9d-94fa5ef81ad2\",\n" +
      "    \"jurisdictionType\": \"MAGISTRATES\",\n" +
      "    \"prosecutionCases\":\n" +
      "    [\n" +
      "      {\n" +
      "        \"caseStatus\": \"ACTIVE\",\n" +
      "        \"defendants\":\n" +
      "        [\n" +
      "          {\n" +
      "            \"id\": \"ac24a1be-939b-49a4-a524-21a3d228f8bc\",\n" +
      "            \"masterDefendantId\": \"ac24a1be-939b-49a4-a524-21a3d228f8bc\",\n" +
      "            \"offences\":\n" +
      "            [\n" +
      "              {\n" +
      "                \"id\": \"3e1218c4-1946-429b-9cb6-f497206405a6\",\n" +
      "                \"modeOfTrial\": \"Summary\",\n" +
      "                \"offenceCode\": \"CA03013\",\n" +
      "                \"offenceDefinitionId\": \"062cedf4-b495-3a6c-9148-cec6bef362ed\",\n" +
      "                \"offenceLegislation\": \"Contrary to section 366(8)(a) and    (9) of the Communications Act 2003.\",\n" +
      "                \"offenceTitle\": \"Obstruct person executing search warrant for TV receiver\",\n" +
      "                \"orderIndex\": 1,\n" +
      "                \"wording\": \"Has a violent past and fear that he will commit further offences and\\n                interfere with witnesse\"\n" +
      "              }\n" +
      "            ],\n" +
      "            \"defenceOrganisation\": {\n" +
      "              \"organisation\": {\n" +
      "                \"name\": \"RAF\"\n" +
      "              }\n" +
      "            },\n" +
      "            \"croNumber\": \"cro number1\",\n" +
      "            \"pncId\": \"pnc number1\",\n" +
      "            \"personDefendant\":\n" +
      "            {\n" +
      "              \"personDetails\":\n" +
      "              {\n" +
      "                \"address\":\n" +
      "                {\n" +
      "                  \"address1\": \"1234\",\n" +
      "                  \"address2\": \"StreetDescription\",\n" +
      "                  \"address3\": \"Locality2O\"\n" +
      "                },\n" +
      "                \"dateOfBirth\": \"1982-06-29\",\n" +
      "                \"documentationLanguageNeeds\": \"ENGLISH\",\n" +
      "                \"firstName\": \"Geoff\",\n" +
      "                \"gender\": \"MALE\",\n" +
      "                \"hearingLanguageNeeds\": \"ENGLISH\",\n" +
      "                \"lastName\": \"Klingon\",\n" +
      "                \"title\": \"Mr\"\n" +
      "              }\n" +
      "            },\n" +
      "            \"prosecutionAuthorityReference\": \"TFL\",\n" +
      "            \"prosecutionCaseId\": \"1d1861ed-e18c-429d-bad0-671802f9cdba\"\n" +
      "          },\n" +
      "          {\n" +
      "            \"id\": \"ac24a1be-939b-49a4-a524-21a3d228f8gd\",\n" +
      "            \"masterDefendantId\": \"ac24a1be-939b-49a4-a524-21a3d228f8aa\",\n" +
      "            \"offences\":\n" +
      "            [\n" +
      "              {\n" +
      "                \"id\": \"3e1218c4-1946-429b-9cb6-f497206405a9\",\n" +
      "                \"modeOfTrial\": \"Summary\",\n" +
      "                \"offenceCode\": \"CA03013\",\n" +
      "                \"offenceDefinitionId\": \"062cedf4-b495-3a6c-9148-cec6bef362ed\",\n" +
      "                \"offenceLegislation\": \"Contrary to section 366(8)(a) and    (9) of the Communications Act 2003.\",\n" +
      "                \"offenceTitle\": \"Obstruct person executing search warrant for TV receiver\",\n" +
      "                \"orderIndex\": 1,\n" +
      "                \"wording\": \"Has a violent past and fear that he will commit further offences and\\n                interfere with witnesse\"\n" +
      "              }\n" +
      "            ],\n" +
      "            \"defenceOrganisation\": {\n" +
      "              \"organisation\": {\n" +
      "                \"name\": \"Royal Navy\",\n" +
      "                \"contact\": {\n" +
      "                  \"primaryEmail\": \"test@test.com\"\n" +
      "                }\n" +
      "              }\n" +
      "            },\n" +
      "            \"croNumber\": \"cro number2\",\n" +
      "            \"pncId\": \"pnc number2\",\n" +
      "            \"personDefendant\":\n" +
      "            {\n" +
      "              \"personDetails\":\n" +
      "              {\n" +
      "                \"address\":\n" +
      "                {\n" +
      "                  \"address1\": \"12345\",\n" +
      "                  \"address2\": \"StreetDescriptionOther\",\n" +
      "                  \"address3\": \"Locality2O\"\n" +
      "                },\n" +
      "                \"dateOfBirth\": \"1982-06-00\",\n" +
      "                \"documentationLanguageNeeds\": \"ENGLISH\",\n" +
      "                \"firstName\": \"Geoff\",\n" +
      "                \"gender\": \"MALE\",\n" +
      "                \"hearingLanguageNeeds\": \"ENGLISH\",\n" +
      "                \"lastName\": \"Klingon\",\n" +
      "                \"title\": \"Mr\"\n" +
      "              }\n" +
      "            },\n" +
      "            \"prosecutionAuthorityReference\": \"TFL\",\n" +
      "            \"prosecutionCaseId\": \"1d1861ed-e18c-429d-bad0-671802f9cdba\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"id\": \"1d1861ed-e18c-429d-bad0-671802f9cdba\",\n" +
      "        \"initiationCode\": \"C\",\n" +
      "        \"originatingOrganisation\": \"0300000\",\n" +
      "        \"prosecutionCaseIdentifier\":\n" +
      "        {\n" +
      "          \"majorCreditorCode\": \"PF30\",\n" +
      "          \"prosecutionAuthorityCode\": \"DERPF\",\n" +
      "          \"prosecutionAuthorityId\": \"bdc190e7-c939-37ca-be4b-9f615d6ef40e\",\n" +
      "          \"prosecutionAuthorityName\": \"Derbyshire Police\",\n" +
      "          \"prosecutionAuthorityOUCode\": \"0300000\",\n" +
      "          \"caseURN\": \"80GD8183221\"\n" +
      "        }\n" +
      "      }\n" +
      "    ],\n" +
      "    \"type\": {\n" +
      "      \"description\": \"Sentence\",\n" +
      "      \"id\": \"5ae4c090-0f70-4694-b4fc-707633d2b430\"\n" +
      "    }\n" +
      "  }\n" +
      "}\n "
  }
}
