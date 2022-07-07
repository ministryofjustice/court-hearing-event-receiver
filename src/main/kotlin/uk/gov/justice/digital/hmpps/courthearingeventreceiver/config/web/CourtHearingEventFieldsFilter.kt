package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.ReadContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryEventType
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.TelemetryService
import java.util.stream.Collectors
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@Component
class CourtHearingEventFieldsFilter(
  @Autowired private val telemetryService: TelemetryService,
  @Autowired
  private val observedFields: ObserveFields
) : Filter {


  override fun doFilter(request: ServletRequest?, response: ServletResponse?, filterChain: FilterChain?) {

    val httpRequest = request as HttpServletRequest
    if (observedFields.fields?.isNotEmpty() !!) {
      val requestWrapper = CustomHttpRequestWrapper(httpRequest)
      filterChain?.doFilter(requestWrapper, response)
      val jsonContext: ReadContext = JsonPath.parse(requestWrapper.inputStream)
      try {
        trackEvent(jsonContext.jsonString(), observedFields)
      }catch(exception: UnsupportedOperationException){
        return
      }
      return
    }
    filterChain?.doFilter(request, response)
  }


  private fun trackEvent(requestJson: String, observedFields: ObserveFields) {
    val (defenceOrganisationAttributes, pncIdExist, croNumberValueExist) = buildEventDetails(requestJson, observedFields)

    telemetryService.trackEvent(
      TelemetryEventType.COMMON_PLATFORM_EVENT_OBSERVED,
      mapOf(
        DEFENCE_ORGANISATION_PATH_KEY to defenceOrganisationAttributes,
        PNC_PATH_KEY to pncIdExist.toString(),
        CRO_PATH_KEY to croNumberValueExist.toString(),
      )
    )
  }

  private fun buildEventDetails(requestJson: String, observedFields: ObserveFields): Triple<String?, Boolean, Boolean> {
    val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(requestJson)

    val defenceOrganisationPath = observedFields.fields?.get(DEFENCE_ORGANISATION_PATH_KEY)
    val defenceOrganisationValues = getPathValue(document, defenceOrganisationPath)?.stream()?.map { item ->
      item.keys
    }?.distinct()?.collect(Collectors.toList())

    val defenceOrganisationAttributes =
      if (defenceOrganisationValues?.isNotEmpty() == true) defenceOrganisationValues.joinToString {values ->  "\'${values}\'" } else NOT_PRESENT

    val pncIdPath = observedFields.fields?.get(PNC_PATH_KEY)
    val pncIdValues = getPathValue(document, pncIdPath)

    val pncIdExist =
      pncIdValues?.isNotEmpty() == true

    val croNumberPath = observedFields.fields?.get(CRO_PATH_KEY)
    val croNumberValues = getPathValue(document, croNumberPath)

    val croNumberExist =
      croNumberValues?.isNotEmpty() == true
    return Triple(defenceOrganisationAttributes, pncIdExist, croNumberExist)
  }

  private fun getPathValue(json: Any, jsonpath: String?): List<Map<String, String>>? {
    return try {
      JsonPath.read(json, jsonpath)
    } catch (exception: PathNotFoundException) {
      log.error(exception.message)
      emptyList()
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(CourtHearingEventFieldsFilter::class.java)
    private const val NOT_PRESENT = "Not Present"
    private const val DEFENCE_ORGANISATION_PATH_KEY = "defenceOrganisation"
    private const val PNC_PATH_KEY = "pnc"
    private const val CRO_PATH_KEY = "cro"
  }
}