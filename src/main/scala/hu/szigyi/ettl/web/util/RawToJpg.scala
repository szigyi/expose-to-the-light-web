package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps

import com.typesafe.scalalogging.StrictLogging

object RawToJpg extends StrictLogging {
  def convert(rawPath: String, jpgPath: String): Unit = {
    logger.warn(s"converting raw '$rawPath' to jpg...")
    s"convert $rawPath -resize 1080 -quality 75 $jpgPath" !
  }

  // https://stackoverflow.com/a/4731270
  def fileNameToJpg(rawName: String): String =
    rawName.replaceAll("\\.[^.]*$", "") + ".JPG"
}

