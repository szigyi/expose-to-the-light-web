package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object EttlRunner extends StrictLogging {

  def executeEttl(dummyCamera: Boolean,
                  setSettings: Boolean,
                  numberOfCaptures: Int,
                  intervalSeconds: Int,
                  rawFileExtension: String,
                  logDirectoryPath: String,
                  rawDirectoryPath: String,
                  logLevel: String): Unit = {
    val ettl = s"""ettl
       |$logLevel
       |$logDirectoryPath
       |${if (dummyCamera) "--dummyCamera" else ""}
       |--imagesBasePath $rawDirectoryPath
       |${if (setSettings) "--setSettings" else ""}
       |--numberOfCaptures $numberOfCaptures
       |--intervalSeconds $intervalSeconds
       |--rawFileExtension $rawFileExtension""".stripMargin

    logger.info(ettl)
    ettl lazyLines_!
  }
}
