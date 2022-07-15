package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.ReadContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
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
    if (HttpMethod.POST.matches(httpRequest.method) && observedFields.fields.isNotEmpty()) {
      val requestWrapper = CustomHttpRequestWrapper(httpRequest)
      filterChain?.doFilter(requestWrapper, response)
      val jsonContext: ReadContext = JsonPath.parse(requestWrapper.inputStream)
      try {
        trackEvent(jsonContext.jsonString(), observedFields)
      } catch (exception: UnsupportedOperationException) {
        log.warn(exception.message)
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
    val eventDetails = mutableMapOf<String, String>()
    observedFields.fields.entries.forEach { field ->
      val values = getPathValues(document, field.value.path)
      if (!field.value.printValue) {
        val exist = values?.isNotEmpty() == true
        eventDetails[field.key] = exist.toString()
      } else {
        eventDetails[field.key] = if (!values.isNullOrEmpty()) values.toString() else "Not Present"
      }
    }
    return eventDetails
  }

  private fun getPathValues(json: Any, jsonpath: String?): List<Map<String, String>>? {
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
