package uk.gov.justice.digital.hmpps.courthearingeventreceiver.config.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObserveFieldDetailsTest {
  @Test
  fun `Given no printValue provided, then default to false`() {
    assertThat(ObserveFieldDetails(path = "foo").printValue).isEqualTo(false)
  }
}
