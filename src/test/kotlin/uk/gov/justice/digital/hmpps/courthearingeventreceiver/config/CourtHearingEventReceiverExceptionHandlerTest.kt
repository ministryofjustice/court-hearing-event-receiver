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

@ExtendWith(MockitoExtension::class)
internal class CourtHearingEventReceiverExceptionHandlerTest {

  private lateinit var exceptionHandler: CourtHearingEventReceiverExceptionHandler

  private val headers = HttpHeaders()

  @Mock
  lateinit var methodArgumentNotValidException: MethodArgumentNotValidException

  @Mock
  lateinit var httpMessageNotReadableException: HttpMessageNotReadableException

  @BeforeEach
  fun beforeEach() {
    exceptionHandler = CourtHearingEventReceiverExceptionHandler()
  }

  @Test
  fun `given invalid method argument when get message then return BAD_REQUEST`() {
    whenever(methodArgumentNotValidException.message).thenReturn("MESSAGE")

    val response = exceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("MESSAGE")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }

  @Test
  fun `given message not readable when handleHttpMessageNotReadable then return BAD_REQUEST`() {
    whenever(httpMessageNotReadableException.message).thenReturn("HTTP message not readable")

    val response = exceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException, headers, HttpStatus.INTERNAL_SERVER_ERROR, ServletWebRequest(MockHttpServletRequest()))

    assertThat(response?.body.toString()).contains("HTTP message not readable")
    assertThat(response?.statusCode?.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
  }
}
