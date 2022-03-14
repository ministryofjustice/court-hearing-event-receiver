package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.isA
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courthearingeventreceiver.service.S3Service
import javax.servlet.FilterChain
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
internal class S3LogFilterTest {

  private lateinit var s3LogFilter: S3LogFilter

  @Mock
  private lateinit var s3Service: S3Service

  @Mock
  private lateinit var request: HttpServletRequest

  @Mock
  private lateinit var response: HttpServletResponse

  @Mock
  private lateinit var inputStream: ServletInputStream

  @Mock
  private lateinit var chain: FilterChain

  @BeforeEach
  fun beforeEach() {
    s3LogFilter = S3LogFilter(s3Service)
  }

  @Test
  fun `when GET method then no logging`() {
    whenever(request.method).thenReturn("GET")

    s3LogFilter.doFilter(request, response, chain)

    verify(chain).doFilter(request, response)
    verifyNoMoreInteractions(chain, s3Service)
  }

  @Test
  fun `when POST method then log body to S3`() {
    whenever(request.method).thenReturn("POST")
    whenever(request.inputStream).thenReturn(inputStream)
    whenever(request.requestURI).thenReturn("/hearing/id/result")
    whenever(request.characterEncoding).thenReturn(CHARSET.name())

    val byteArray = "Hello World".toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)

    s3LogFilter.doFilter(request, response, chain)

    verify(chain).doFilter(isA<CustomHttpRequestWrapper>(), same(response))
    verify(s3Service).uploadMessage("/hearing/id/result", "Hello World")
    verifyNoMoreInteractions(chain, s3Service)
  }

  companion object {
    private val CHARSET = Charsets.UTF_8
  }
}
