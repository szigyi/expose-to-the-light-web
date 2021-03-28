package hu.szigyi.ettl.web.util

import hu.szigyi.ettl.web.util.LocalTimeUtil.translateTimeToAnotherZone
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{LocalTime, ZoneId}

class LocalTimeUtilSpec extends AnyFreeSpec with Matchers {

  "should translate GMT+1 local time to utc local time" in {
    val lt = LocalTime.of(10, 0, 0)
    translateTimeToAnotherZone(lt, ZoneId.of("GMT+1"), ZoneId.of("UTC")).toString shouldBe "09:00"
  }

  "should translate utc local time to GMT+1 local time" in {
    val lt = LocalTime.of(10, 0, 0)
    translateTimeToAnotherZone(lt, ZoneId.of("UTC"), ZoneId.of("GMT+1")).toString shouldBe "11:00"
  }
}
