package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object EttlRunner extends StrictLogging {

  def executeEttl(dummyCamera: Boolean, intervalSeconds: Int): Unit = {
    val ettl = s"""ettl
       |/Users/szabolcs/dev/expose-to-the-light/logs
       |${if (dummyCamera) "--dummyCamera"}
       |--imagesBasePath /Users/szabolcs/dev/expose-to-the-light/captured-images
       |--setSettings
       |--numberOfCaptures 5
       |--intervalSeconds $intervalSeconds
       |--rawFileExtension JPG""".stripMargin

    logger.info(ettl)
    ettl lazyLines_!
  }
}
