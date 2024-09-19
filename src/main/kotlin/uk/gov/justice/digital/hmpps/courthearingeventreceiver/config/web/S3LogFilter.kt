package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.S3Service

private val SUPPORTED_HTTP_METHODS = setOf(HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT)

@Component
class S3LogFilter(@Autowired private val s3Service: S3Service) : Filter {

  override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
    val httpRequest = request as HttpServletRequest
    if (SUPPORTED_HTTP_METHODS.contains(HttpMethod.valueOf(httpRequest.method))) {
      val requestWrapper = CustomHttpRequestWrapper(httpRequest)
      filterChain.doFilter(requestWrapper, response)
      s3Service.uploadMessage(request.requestURI, requestWrapper.requestBody)
      return
    }
    filterChain.doFilter(request, response)
  }
}
