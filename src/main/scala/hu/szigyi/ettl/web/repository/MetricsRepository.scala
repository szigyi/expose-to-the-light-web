package hu.szigyi.ettl.web.repository

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.repository.MetricsRepository.{TimeResidualDomain, timeResidualStorage}

import java.time.LocalTime
import scala.concurrent.duration.Duration

class MetricsRepository extends StrictLogging {
  def getAllTimeResiduals: Seq[TimeResidualDomain] = {
    logger.trace(s"Size of time residual storage: ${timeResidualStorage.size}")
    timeResidualStorage
  }

  def addResidual(r: TimeResidualDomain): Unit = {
    logger.debug(s"Adding to time residual storage: $r")
    timeResidualStorage = timeResidualStorage :+ r
  }
}

object MetricsRepository {
  case class TimeResidualDomain(orderNumber: String, difference: Duration, actual: LocalTime, expected: LocalTime)
  private var timeResidualStorage: Seq[TimeResidualDomain] = Seq.empty
}