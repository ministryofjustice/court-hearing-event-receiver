package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.ServletWebRequest
import tools.jackson.core.JsonParser
import tools.jackson.databind.exc.MismatchedInputException

@ExtendWith(MockitoExtension::class)
internal class CourtHearingEventReceiverExceptionHandlerTest {

  private lateinit var exceptionHandler: CourtHearingEventReceiverExceptionHandler

  private val headers = HttpHeaders()

  @Mock
  lateinit var methodArgumentNotValidException: MethodArgumentNotValidException

  @Mock
  lateinit var messageNotReadableException: HttpMessageNotReadableException

  @Mock
  lateinit var jsonParser: JsonParser

  @BeforeEach
  fun beforeEach() {
    exceptionHandler = CourtHearingEventReceiverExceptionHandler()
  }

  @Test
  fun `given Jackson MismatchedInputException when handleJacksonMismatchedInputException then return INTERNAL_SERVER_ERROR with message`() {
    val mismatchedException = MismatchedInputException.from(
      jsonParser,
      String::class.java,
      "Missing required creator property 'isPncMissing'",
    )

    val response = exceptionHandler.handleJacksonMismatchedInputException(mismatchedException)

    assertThat(response.statusCode.value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
    assertThat(response.body?.userMessage).isEqualTo("Invalid JSON structure")
    assertThat(response.body?.developerMessage).contains("Missing required creator property 'isPncMissing'")
  }

  @Test
  fun `given invalid method argument when get message then return BAD_REQUEST`() {
    whenever(methodArgumentNotValidException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("MESSAGE")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }

  @Test
  fun `given message not readable with Jackson cause when get message then return INTERNAL_SERVER_ERROR with error`() {
    val mismatchedException = MismatchedInputException.from(
      jsonParser,
      String::class.java,
      "Missing required creator property 'isPncMissing'",
    )
    whenever(messageNotReadableException.cause).thenReturn(mismatchedException)
    whenever(messageNotReadableException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleHttpMessageNotReadable(
      messageNotReadableException,
      headers,
      HttpStatus.INTERNAL_SERVER_ERROR,
      ServletWebRequest(MockHttpServletRequest()),
    )

    assertThat(response?.body.toString()).contains("Invalid request body")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
  }

  @Test
  fun `given message not readable when get message then return INTERNAL_SERVER_ERROR`() {
    whenever(messageNotReadableException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleHttpMessageNotReadable(messageNotReadableException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("MESSAGE")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
  }
}
