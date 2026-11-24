package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import io.sentry.Sentry
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestControllerAdvice
class CourtHearingEventReceiverExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(e: Exception): ResponseEntity<ErrorResponse> {
    log.info("Validation exception: {}", e.message)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Validation failure: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  @ExceptionHandler(java.lang.Exception::class)
  fun handleException(e: java.lang.Exception): ResponseEntity<ErrorResponse> {
    log.error("Unexpected exception", e)

    Sentry.captureException(e)

    return ResponseEntity
      .status(INTERNAL_SERVER_ERROR)
      .body(
        ErrorResponse(
          status = INTERNAL_SERVER_ERROR,
          userMessage = "Unexpected error: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  public override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
    log.error("Unexpected exception", ex)
    Sentry.captureException(ex)
    val response = ErrorResponse(status = 400, developerMessage = ex.message, userMessage = ex.message)
    return ResponseEntity(response, BAD_REQUEST)
  }

  public override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
    log.error("Unexpected exception", ex)

    Sentry.captureException(ex)

    val response = ErrorResponse(status = 400, developerMessage = ex.message, userMessage = ex.message)
    return ResponseEntity(response, BAD_REQUEST)
  }

  companion object {
    private val log = LoggerFactory.getLogger(CourtHearingEventReceiverExceptionHandler::class.java)
  }
}
