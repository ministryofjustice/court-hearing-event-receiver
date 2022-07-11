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
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class CourtHearingEventFieldsFilter(
  @Autowired private val telemetryService: TelemetryService,
  @Autowired private val observedFields: ObserveFields
) : Filter {

  override fun doFilter(request: ServletRequest?, response: ServletResponse?, filterChain: FilterChain?) {

    val httpRequest = request as HttpServletRequest
    if (observedFields.fields?.isNotEmpty()!!) {
      val requestWrapper = CustomHttpRequestWrapper(httpRequest)
      filterChain?.doFilter(requestWrapper, response)
      val jsonContext: ReadContext = JsonPath.parse(requestWrapper.inputStream)
      try {
        trackEvent(jsonContext.jsonString(), observedFields)
      } catch (exception: UnsupportedOperationException) {
        return
      }

      return
    }
    filterChain?.doFilter(request, response)
  }

  private fun trackEvent(requestJson: String, observedFields: ObserveFields) {
    telemetryService.trackEvent(
      TelemetryEventType.COMMON_PLATFORM_EVENT_OBSERVED,
      buildEventDetails(requestJson, observedFields)
    )
  }

  private fun buildEventDetails(requestJson: String, observedFields: ObserveFields): MutableMap<String, String> {
    val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(requestJson)
    val fieldExistMap = mutableMapOf<String, String>()
    observedFields.fields?.entries!!.forEach { field ->
      val exist = getPathValue(document, field.value)?.isNotEmpty() == true
      fieldExistMap[field.key] = exist.toString()
    }
    return fieldExistMap
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
  }
}
