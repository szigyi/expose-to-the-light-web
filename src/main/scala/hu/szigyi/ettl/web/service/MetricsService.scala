package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.service.LogService.LogLine
import hu.szigyi.ettl.web.service.MetricsService.{TimeResidual, logLinesToTimeResiduals}

import java.time.{Instant, LocalTime, ZoneId}
import java.time.temporal.ChronoUnit.MILLIS
import scala.concurrent.duration._
import scala.util.Try

class MetricsService(logService: LogService) {

  def getLatestTimeResiduals: Seq[TimeResidual] =
    logLinesToTimeResiduals(logService.readLatestLog).sortBy(_.orderNumber).reverse

  def getLatestTimeResidualsSince(since: Instant): Seq[TimeResidual] =
    getLatestTimeResiduals
      .filter(_.actual.isAfter(LocalTime.from(since.atZone(ZoneId.systemDefault()))))
}

object MetricsService {
  case class TimeResidual(orderNumber: Int, difference: Duration, actual: LocalTime, expected: LocalTime)

  def logLinesToTimeResiduals(lines: Seq[LogLine]): Seq[TimeResidual] = {
    (findInterval(lines), findStartTime(lines)) match {
      case (Some(intervalSeconds), Some(scheduleStartsAt)) =>
        lines.filter(_.message.contains("Taking photo")).toList match {
          case Nil => Seq.empty
          case photosTaken =>
            photosTaken.flatMap { photo =>
              findOrderNumber(photo.message).map { orderNumber =>
                val expectedTime = scheduleStartsAt.plusSeconds((orderNumber - 1) * intervalSeconds)
                val diff         = MILLIS.between(expectedTime, photo.time)
                TimeResidual(orderNumber, Duration(diff, MILLISECONDS), photo.time, expectedTime)
              }
            }
        }
      case _ =>
        Seq.empty
    }
  }

  private def findInterval(lines: Seq[LogLine]): Option[Int] =
    lines
      .find(_.message.contains("Interval:"))
      .flatMap(intervalLine => {
        "[^Interval: ][0-9]*".r.findFirstIn(intervalLine.message).flatMap(i => Try(i.toInt).toOption)
      })

  private def findStartTime(lines: Seq[LogLine]): Option[LocalTime] =
    lines.find(_.message.contains("Schedule starts:")).map(_.time)

  private def findOrderNumber(message: String): Option[Int] =
    "[^\\[][0-9]*".r.findFirstIn(message).flatMap(s => Try(s.toInt).toOption)
}
