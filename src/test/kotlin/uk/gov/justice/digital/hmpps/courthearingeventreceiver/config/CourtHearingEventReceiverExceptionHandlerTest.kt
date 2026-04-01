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
  fun `given Jackson MismatchedInputException when handleJacksonMismatchedInputException then return BAD_REQUEST with detailed message`() {
    val mismatchedException = MismatchedInputException.from(
      jsonParser,
      String::class.java,
      "Missing required creator property 'isPncMissing'",
    )

    val response = exceptionHandler.handleJacksonMismatchedInputException(mismatchedException)

    assertThat(response.statusCode.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
    assertThat(response.body?.userMessage).contains("Invalid JSON structure")
    assertThat(response.body?.developerMessage).contains("Jackson deserialization failed")
    assertThat(response.body?.moreInfo).contains("required fields")
  }

  @Test
  fun `given invalid method argument when get message then return BAD_REQUEST`() {
    whenever(methodArgumentNotValidException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("MESSAGE")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }

  @Test
  fun `given message not readable with Jackson cause when get message then return BAD_REQUEST with detailed error`() {
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

    assertThat(response?.body.toString()).contains("Invalid JSON structure")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }

  @Test
  fun `given message not readable when get message then return BAD_REQUEST`() {
    whenever(messageNotReadableException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleHttpMessageNotReadable(messageNotReadableException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("MESSAGE")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }
}
