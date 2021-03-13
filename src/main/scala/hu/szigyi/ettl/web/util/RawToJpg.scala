package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

import java.io.File

object RawToJpg extends StrictLogging {
  // TODO this command is not working in this form
  def convert(rawPath: String, jpgPath: String): Unit = {
    logger.warn(s"converting raw '$rawPath' to jpg...")
    s"convert $rawPath -resize 1080 -quality 75 $jpgPath" !
  }

  // https://stackoverflow.com/a/4731270
  def fileNameToJpg(path: String): String =
    path.replaceAll("\\.[^.]*$", "") + ".JPG"

  def fileName(path: String): String =
    new File(path).getName
}