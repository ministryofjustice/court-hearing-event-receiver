package uk.gov.justice.digital.hmpps.courthearingeventreceiver.service

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectResult
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argForWhich
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.File
import java.util.Date

@ExtendWith(MockitoExtension::class)
internal class S3ServiceTest {

  private lateinit var minimalJson: String

  @Mock
  lateinit var amazonS3Client: AmazonS3Client

  private lateinit var s3Service: S3Service

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
    val putResult: PutObjectResult = PutObjectResult().apply {
      expirationTime = Date()
      eTag = "ETAG"
    }
    whenever(amazonS3Client.putObject(eq("bucket-name"), argForWhich { startsWith("cp/RESULT/UNKNOWN_COURT/") }, eq("message")))
      .thenReturn(putResult)

    val eTag = s3Service.uploadMessage("/hearing/$UUID/result", "message")

    verify(amazonS3Client).putObject(eq("bucket-name"), argForWhich { startsWith("cp/RESULT/UNKNOWN_COURT/") }, eq("message"))
    verifyNoMoreInteractions(amazonS3Client)
    assertThat(eTag).isEqualTo("ETAG")
  }

  @Test
  fun `given normal JSON input then message is uploaded as file`() {
    val putResult: PutObjectResult = PutObjectResult().apply {
      expirationTime = Date()
      eTag = "ETAG"
    }
    whenever(amazonS3Client.putObject(eq("bucket-name"), argForWhich { startsWith("cp/CONFIRM_UPDATE/B10JQ00/") }, any<String>()))
      .thenReturn(putResult)

    val eTag = s3Service.uploadMessage("/hearing/$UUID", minimalJson)

    verify(amazonS3Client).putObject(eq("bucket-name"), argForWhich { startsWith("cp/CONFIRM_UPDATE/B10JQ00/") }, any<String>())
    verifyNoMoreInteractions(amazonS3Client)
    assertThat(eTag).isEqualTo("ETAG")
  }

  companion object {
    const val UUID = "b9ebd552-8122-448d-8a11-e9c946a5cc0a"
  }
}
