package hu.szigyi.ettl.web.service

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalTime

class LogServiceSpec extends AnyFreeSpec with Matchers {

  "should parse" in {
    LogService.parseLocalTime("00:07:53.526").get shouldBe LocalTime.parse("00:07:53.526")
  }
}
