package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest

@ExtendWith(MockitoExtension::class)
internal class CustomHttpRequestWrapperTest {

  private lateinit var requestWrapper: CustomHttpRequestWrapper

  @Mock
  private lateinit var request: HttpServletRequest

  @Mock
  private lateinit var inputStream: ServletInputStream

  @BeforeEach
  fun beforeEach() {
    whenever(request.inputStream).thenReturn(inputStream)
    whenever(request.characterEncoding).thenReturn(CHARSET.name())

    val byteArray = "Hello World".toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)
    requestWrapper = CustomHttpRequestWrapper(request)
  }

  @Test
  fun `when create then set requestBody`() {
    val byteArray = "Hello World".toByteArray(CHARSET)
    whenever(inputStream.readAllBytes()).thenReturn(byteArray)

    val requestWrapper = CustomHttpRequestWrapper(request)

    assertThat(requestWrapper.requestBody).isEqualTo("Hello World")
  }

  @Test
  fun `when getInputStream then return`() {
    val servletInputStream = requestWrapper.inputStream

    assertThat(servletInputStream.isReady).isTrue
    assertThat(servletInputStream.isFinished).isFalse
    assertThat(servletInputStream.available()).isGreaterThan(10)
    // H is 72
    assertThat(servletInputStream.read()).isEqualTo(72)
    while (servletInputStream.read() > 0) {
      assertThat(servletInputStream.isFinished).isFalse
    }
    assertThat(servletInputStream.isFinished).isTrue
    assertThat(servletInputStream.available()).isLessThanOrEqualTo(0)
  }

  @Test
  fun `when getReader then return`() {
    val bufferedReader = requestWrapper.getReader()

    assertThat(bufferedReader.read()).isEqualTo(72)
  }

  companion object {
    private val CHARSET = Charsets.UTF_8
  }
}
