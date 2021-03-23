package hu.szigyi.ettl.web.repository

import hu.szigyi.ettl.web.repository.MetricsRepository.{TimeResidualDomain, timeResidualStorage}

import java.time.LocalTime
import scala.concurrent.duration.Duration

class MetricsRepository {
  def getAllTimeResiduals: Seq[TimeResidualDomain] =
    timeResidualStorage

  def addResidual(r: TimeResidualDomain): Unit =
    timeResidualStorage = timeResidualStorage :+ r
}

object MetricsRepository {
  case class TimeResidualDomain(difference: Duration, actual: LocalTime, expected: LocalTime)
  private var timeResidualStorage: Seq[TimeResidualDomain] = Seq.empty
}