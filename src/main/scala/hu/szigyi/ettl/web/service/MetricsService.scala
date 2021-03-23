package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.repository.MetricsRepository.TimeResidualDomain
import hu.szigyi.ettl.web.service.LogService.LogLine
import hu.szigyi.ettl.web.service.MetricsService.logLinesToTimeResiduals

import java.time.{Instant, LocalTime, ZoneId}
import java.time.temporal.ChronoUnit.MILLIS
import scala.concurrent.duration._

class MetricsService(logService: LogService) {

  def getLatestTimeResiduals(intervalSeconds: Int): Seq[TimeResidualDomain] =
    logLinesToTimeResiduals(intervalSeconds, logService.readLatestLog.reverse)

  def getLatestTimeResidualsSince(intervalSeconds: Int, since: Instant): Seq[TimeResidualDomain] =
    getLatestTimeResiduals(intervalSeconds)
      .filter(_.actual.isAfter(LocalTime.from(since.atZone(ZoneId.systemDefault()))))
}

object MetricsService {
  // TODO read interval secs from log
  def logLinesToTimeResiduals(intervalSeconds: Int, lines: Seq[LogLine]): Seq[TimeResidualDomain] = {
    lines.filter(_.message.contains("Taking photo")).toList match {
      case Nil => Seq.empty
      case photosTaken =>
        val baseLine = photosTaken.head.time
        photosTaken.zipWithIndex.map {
          case (photo, index) => {
            val orderNumber = "[^\\[][0-9]*".r.findFirstIn(photo.message).getOrElse("")
            val expectedTime = baseLine.plusSeconds(index * intervalSeconds)
            val diff         = MILLIS.between(expectedTime, photo.time)
            TimeResidualDomain(orderNumber, Duration(diff, MILLISECONDS), photo.time, expectedTime)
          }
        }
    }
  }
}
