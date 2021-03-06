package hu.szigyi.ettl.web.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class LogServiceSpec extends AnyFreeSpec with Matchers {

  "should parse" in {
    LogService.parseLogInstant("2021-03-06 00:07:53.526").get shouldBe Instant.parse("2021-03-06T00:07:53.526Z")
  }
}
