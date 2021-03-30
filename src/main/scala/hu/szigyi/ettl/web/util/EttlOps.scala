package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object EttlOps extends StrictLogging {

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

  def stopEttl(): Unit = {
    logger.info("Interrupting command line ettl")
    ("ps ax" #| "grep java" #| "grep -v 'grep'" #| "grep expose-to-the-light_2" #| "cut -d '?' -f1" #| "xargs kill -2").!
  }
}
