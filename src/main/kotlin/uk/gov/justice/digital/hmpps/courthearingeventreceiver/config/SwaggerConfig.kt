package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config

import com.google.common.base.Predicates
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.Optional
import java.util.Properties

@Configuration
@EnableSwagger2
class SwaggerConfig {

  @Autowired
  private lateinit var applicationContext: ApplicationContext

  @Bean
  fun courtHearingEventReceiverSwagger(): Docket {
    val docket = Docket(DocumentationType.SWAGGER_2)
      .useDefaultResponseMessages(false)
      .apiInfo(apiInfo())
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(
        Predicates.or(
          mutableListOf(
            PathSelectors.regex("(\\/ping)"),
            PathSelectors.regex("(\\/info)"),
            PathSelectors.regex("(\\/health)")
          )
        )
      )
      .build()
    docket.genericModelSubstitutes(Optional::class.java)
    return docket
  }

  private fun getVersion(): BuildProperties {
    return try {
      applicationContext.getBean("buildProperties") as BuildProperties
    } catch (be: BeansException) {
      val properties = Properties()
      properties["version"] = "?"
      BuildProperties(properties)
    }
  }

  private fun contactInfo(): Contact {
    return Contact(
      "HMPPS Probation in Court Team",
      "",
      "john.evans+pictsupport@digital.justice.gov.uk"
    )
  }

  private fun apiInfo(): ApiInfo? {
    val vendorExtension = StringVendorExtension("", "")
    val vendorExtensions: MutableCollection<VendorExtension<*>> = ArrayList()
    vendorExtensions.add(vendorExtension)
    return ApiInfo(
      "Court Hearing Event Receiver API Documentation",
      "REST service for receiving updates to court hearing information",
      getVersion().version,
      "https://gateway.nomis-api.service.justice.gov.uk/auth/terms",
      contactInfo(),
      "Open Government Licence v3.0", "https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/", vendorExtensions
    )
  }
}
