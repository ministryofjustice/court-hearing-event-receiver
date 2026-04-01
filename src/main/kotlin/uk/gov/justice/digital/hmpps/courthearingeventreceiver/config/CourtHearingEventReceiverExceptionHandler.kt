package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

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
    val missingProperty = e.path.lastOrNull()?.from()?.toString() ?: "unknown"
    val targetType = e.targetType?.simpleName ?: "unknown type"

    log.error(
      "Jackson deserialization error - Missing property '{}' in type '{}'. This may indicate a version mismatch in JSON structure or incorrect field mapping. Path: {}",
      missingProperty,
      targetType,
      e.pathReference,
      e,
    )

    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Invalid JSON structure: missing required field '$missingProperty' in $targetType",
          developerMessage = "Jackson deserialization failed: ${e.message}. Path: ${e.pathReference}",
          moreInfo = "Check that all required fields are present and the JSON structure matches the expected schema",
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
    val response = ErrorResponse(status = 400, developerMessage = ex.message, userMessage = ex.message)
    return ResponseEntity(response, BAD_REQUEST)
  }

  public override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
    // Check if this is a Jackson deserialization error
    val cause = ex.cause
    if (cause is MismatchedInputException) {
      // Extract property name from error message as Jackson 3.x API has changed
      val missingProperty = cause.message?.let { msg ->
        val match = Regex("property '([^']+)'").find(msg)
        match?.groupValues?.getOrNull(1)
      } ?: "unknown"
      val targetType = cause.targetType?.simpleName ?: "unknown type"

      log.error(
        "HTTP message not readable due to Jackson deserialization error - Missing property '{}' in type '{}'. Path: {}",
        missingProperty,
        targetType,
        cause.pathReference,
        ex,
      )

      val response = ErrorResponse(
        status = 400,
        developerMessage = "Jackson deserialization failed: ${cause.message}. Path: ${cause.pathReference}",
        userMessage = "Invalid JSON structure: missing required field '$missingProperty' in $targetType",
        moreInfo = "Check that all required fields are present and the JSON structure matches the expected schema",
      )
      return ResponseEntity(response, BAD_REQUEST)
    }

    log.error("HTTP message not readable", ex)
    val response = ErrorResponse(status = 400, developerMessage = ex.message, userMessage = ex.message)
    return ResponseEntity(response, BAD_REQUEST)
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
