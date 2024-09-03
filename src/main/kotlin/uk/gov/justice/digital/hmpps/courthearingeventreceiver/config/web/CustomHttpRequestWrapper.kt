package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class CustomHttpRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

  var requestBody: String

  init {
    requestBody = readInputStreamInStringFormat(request.inputStream, Charset.forName(request.characterEncoding))
  }

  private fun readInputStreamInStringFormat(inputStream: InputStream, charset: Charset): String {
    return String(inputStream.readAllBytes(), charset)
  }

  @Throws(IOException::class)
  override fun getReader(): BufferedReader {
    return BufferedReader(InputStreamReader(this.getInputStream()))
  }

  @Throws(IOException::class)
  override fun getInputStream(): ServletInputStream {
    val byteArrayInputStream = ByteArrayInputStream(requestBody.encodeToByteArray())
    return object : ServletInputStream() {
      private var finished = false
      override fun isFinished(): Boolean {
        return finished
      }

      @Throws(IOException::class)
      override fun available(): Int {
        return byteArrayInputStream.available()
      }

      @Throws(IOException::class)
      override fun close() {
        super.close()
        byteArrayInputStream.close()
      }

      override fun isReady(): Boolean {
        return true
      }

      override fun setReadListener(readListener: ReadListener) {
        throw UnsupportedOperationException()
      }

      @Throws(IOException::class)
      override fun read(): Int {
        val data = byteArrayInputStream.read()
        if (data == -1) {
          finished = true
        }
        return data
      }
    }
  }
}
