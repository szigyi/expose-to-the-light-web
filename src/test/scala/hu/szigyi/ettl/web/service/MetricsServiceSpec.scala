package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.service.MetricsService.TimeResidual
import hu.szigyi.ettl.web.tool.LogAnalyser
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import java.time.LocalTime.{parse => localTime}

class MetricsServiceSpec extends AnyFreeSpec with Matchers {

  "when interval and schedule starts are present with Taking photo then calculates time residuals" in {
    val logs =
      """
21:23:34.578:::I:::          Interval: 2 seconds
21:23:34.939:::I:::Schedule starts: 2021-03-25T21:23:34.939575Z
21:23:36.413:::I:::[1/10] Taking photo...
21:23:37.896:::I:::[2/10] Taking photo...
21:23:38.979:::I:::[3/10] Taking photo...
21:23:40.962:::I:::[4/10] Taking photo...
21:23:42.929:::I:::[5/10] Taking photo...
21:23:44.893:::I:::[6/10] Taking photo...
21:23:46.984:::I:::[7/10] Taking photo...
21:23:48.954:::I:::[8/10] Taking photo...
21:23:50.960:::I:::[9/10] Taking photo...
21:23:52.924:::I:::[10/10] Taking photo...
        """
    val logLines = LogAnalyser.stringToLogLines(logs)

    val result = MetricsService.logLinesToTimeResiduals(logLines)

    result shouldBe Seq(
      TimeResidual(1, 1474.millis, localTime("21:23:36.413"), localTime("21:23:34.939")),
      TimeResidual(2, 957.millis, localTime("21:23:37.896"), localTime("21:23:36.939")),
      TimeResidual(3, 40.millis, localTime("21:23:38.979"), localTime("21:23:38.939")),
      TimeResidual(4, 23.millis, localTime("21:23:40.962"), localTime("21:23:40.939")),
      TimeResidual(5, -10.millis, localTime("21:23:42.929"), localTime("21:23:42.939")),
      TimeResidual(6, -46.millis, localTime("21:23:44.893"), localTime("21:23:44.939")),
      TimeResidual(7, 45.millis, localTime("21:23:46.984"), localTime("21:23:46.939")),
      TimeResidual(8, 15.millis, localTime("21:23:48.954"), localTime("21:23:48.939")),
      TimeResidual(9, 21.millis, localTime("21:23:50.960"), localTime("21:23:50.939")),
      TimeResidual(10, -15.millis, localTime("21:23:52.924"), localTime("21:23:52.939")),
    )
  }
}
