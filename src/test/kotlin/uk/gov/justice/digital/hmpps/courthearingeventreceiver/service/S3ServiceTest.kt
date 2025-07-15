package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

@ExtendWith(MockitoExtension::class)
internal class S3ServiceTest {

  private lateinit var minimalJson: String

  @Mock
  lateinit var amazonS3Client: S3AsyncClient

  private lateinit var s3Service: S3Service

  @Captor
  lateinit var putObjectRequestCaptor: ArgumentCaptor<PutObjectRequest>

  @Captor
  lateinit var requestBodyCaptor: ArgumentCaptor<AsyncRequestBody>

  @BeforeEach
  fun setUp() {
    val mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
    mapper.registerModule(KotlinModule.Builder().build())
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    s3Service = S3Service("bucket-name", amazonS3Client, mapper)
    minimalJson = File("src/test/resources/json/court-application-minimal.json").readText(Charsets.UTF_8)
  }

  @Test
  fun `given broken JSON input then message is uploaded as file`() {
    val putResult: PutObjectResponse = PutObjectRequest.builder().build().let {
      PutObjectResponse.builder().expiration(Date().toString()).eTag("ETAG").build()
    }

    whenever(amazonS3Client.putObject(any<PutObjectRequest>(), any<AsyncRequestBody>())).thenReturn(CompletableFuture.completedFuture(putResult))

    val eTag = s3Service.uploadMessage("/hearing/$UUID/result", "message")

    verify(amazonS3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture())
    assertThat(putObjectRequestCaptor.value.bucket()).isEqualTo("bucket-name")
    assertThat(putObjectRequestCaptor.value.key()).startsWith("cp/RESULT/UNKNOWN_COURT/")
    assertThat(requestBodyCaptor.value.contentLength().get()).isEqualTo(7)

    // Check the content of the body
    val capturedBody: AsyncRequestBody = requestBodyCaptor.value
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(1024)
    capturedBody.subscribe(Consumer { byteBuffer.put(it) })
    byteBuffer.flip()
    val contentOfBody: String = Charsets.UTF_8.decode(byteBuffer).toString()

    assertThat(contentOfBody).isEqualTo("message")

    verifyNoMoreInteractions(amazonS3Client)
    assertThat(eTag).isEqualTo("ETAG")
  }

  @Test
  fun `given normal JSON input then message is uploaded as file`() {
    val putResult: PutObjectResponse = PutObjectRequest.builder().build().let {
      PutObjectResponse.builder().expiration(Date().toString()).eTag("ETAG").build()
    }

    whenever(amazonS3Client.putObject(any<PutObjectRequest>(), any<AsyncRequestBody>())).thenReturn(CompletableFuture.completedFuture(putResult))

    val eTag = s3Service.uploadMessage("/hearing/$UUID", minimalJson)

    verify(amazonS3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture())
    assertThat(putObjectRequestCaptor.value.bucket()).isEqualTo("bucket-name")
    assertThat(putObjectRequestCaptor.value.key()).startsWith("cp/CONFIRM_UPDATE/B10JQ00/")
    assertThat(requestBodyCaptor.value.contentLength().get()).isEqualTo(4733L)

    // Check the content of the body
    val capturedBody: AsyncRequestBody = requestBodyCaptor.value
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(4733)
    capturedBody.subscribe(Consumer { byteBuffer.put(it) })
    byteBuffer.flip()
    val contentOfBody: String = Charsets.UTF_8.decode(byteBuffer).toString()

    assertThat(contentOfBody).isEqualTo(minimalJson)

    verifyNoMoreInteractions(amazonS3Client)
    assertThat(eTag).isEqualTo("ETAG")
  }

  companion object {
    const val UUID = "b9ebd552-8122-448d-8a11-e9c946a5cc0a"
  }
}
