package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object EttlRunner extends StrictLogging {

  def executeEttl(dummyCamera: Boolean,
                  setSettings: Boolean,
                  numberOfCaptures: Int,
                  intervalSeconds: Int,
                  rawFileExtension: String): Unit = {
    val ettl = s"""ettl
       |/Users/szabolcs/dev/expose-to-the-light/logs
       |${if (dummyCamera) "--dummyCamera" else ""}
       |--imagesBasePath /Users/szabolcs/dev/expose-to-the-light/captured-images
       |${if (setSettings) "--setSettings" else ""}
       |--numberOfCaptures $numberOfCaptures
       |--intervalSeconds $intervalSeconds
       |--rawFileExtension $rawFileExtension""".stripMargin

    logger.info(ettl)
    ettl lazyLines_!
  }
}
