package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object EttlRunner extends StrictLogging {

  def executeEttl(intervalSeconds: Int): Unit = {
    logger.info(s"Running ettl $intervalSeconds")
    s"""ettl
       |/Users/szabolcs/dev/expose-to-the-light/logs
       |--dummyCamera
       |--imagesBasePath /Users/szabolcs/dev/expose-to-the-light/captured-images
       |--setSettings
       |--numberOfCaptures 5
       |--intervalSeconds $intervalSeconds
       |--rawFileExtension JPG""".stripMargin lazyLines_!
  }
}
