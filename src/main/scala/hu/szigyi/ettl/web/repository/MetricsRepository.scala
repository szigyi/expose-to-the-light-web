package hu.szigyi.ettl.web.repository

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.repository.MetricsRepository.timeResidualStorage
import hu.szigyi.ettl.web.service.MetricsService.TimeResidualDomain

class MetricsRepository extends StrictLogging {
  def getAllTimeResiduals: Seq[TimeResidualDomain] = {
    logger.trace(s"Size of time residual storage: ${timeResidualStorage.size}")
    timeResidualStorage
  }

  def addResidual(r: TimeResidualDomain): Unit = {
    logger.debug(s"Adding to time residual storage: $r")
    timeResidualStorage = (timeResidualStorage :+ r).sortBy(_.orderNumber)
  }
}

object MetricsRepository {

  private var timeResidualStorage: Seq[TimeResidualDomain] = Seq.empty
}