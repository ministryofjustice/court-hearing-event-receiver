package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import io.sentry.Sentry
import io.sentry.SentryLevel
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
import tools.jackson.databind.exc.MismatchedInputException

@RestControllerAdvice
class CourtHearingEventReceiverExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(MismatchedInputException::class)
  fun handleJacksonMismatchedInputException(e: MismatchedInputException): ResponseEntity<ErrorResponse> {
    log.error("Jackson deserialization failed", e)
    
    return ResponseEntity
      .status(INTERNAL_SERVER_ERROR)
      .body(
        ErrorResponse(
          status = INTERNAL_SERVER_ERROR,
          userMessage = "Invalid JSON structure",
          developerMessage = e.message,
        ),
      )
  }

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

    // Capture all unexpected exceptions to Sentry
    Sentry.withScope { scope ->
      scope.setTag("error.type", "unexpected_exception")
      scope.setTag("exception.class", e.javaClass.simpleName)
      scope.level = SentryLevel.ERROR
      Sentry.captureException(e)
    }

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

  public override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
    log.error("Unexpected exception", ex)

    // Capture validation errors to Sentry
    Sentry.withScope { scope ->
      scope.setTag("error.type", "method_argument_not_valid")
      scope.level = SentryLevel.WARNING
      Sentry.captureException(ex)
    }

    val response = ErrorResponse(status = 400, developerMessage = ex.message, userMessage = ex.message)
    return ResponseEntity(response, BAD_REQUEST)
  }

  public override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
    log.error("HTTP message not readable", ex)

    val response = ErrorResponse(
      status = INTERNAL_SERVER_ERROR,
      userMessage = "Invalid request body",
      developerMessage = ex.message,
    )
    return ResponseEntity(response, INTERNAL_SERVER_ERROR)
  }


  companion object {
    private val log = LoggerFactory.getLogger(CourtHearingEventReceiverExceptionHandler::class.java)
  }
}

data class ErrorResponse(
  val status: Int,
  val errorCode: Int? = null,
  val userMessage: String? = null,
  val developerMessage: String? = null,
  val moreInfo: String? = null,
) {
  constructor(
    status: HttpStatus,
    errorCode: Int? = null,
    userMessage: String? = null,
    developerMessage: String? = null,
    moreInfo: String? = null,
  ) :
    this(status.value(), errorCode, userMessage, developerMessage, moreInfo)
}
