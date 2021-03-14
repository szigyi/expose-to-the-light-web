package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

import java.io.File

object RawToJpg extends StrictLogging {
  val jpgNameAddition: String = ".JPG"

  def convertToJpg(rawPath: String): String = {
    val jpgPath = filePathToJpg(rawPath)
    logger.debug(s"converting raw '$rawPath' to '$jpgPath'...")
    val exifCommand =
      s"exiftool -b -PreviewImage $rawPath -w $jpgNameAddition"
    val magickCommand =
      s"magick convert $jpgPath -resize 1080 -quality 75 $jpgPath"

    exifCommand !

    magickCommand !

    jpgPath
  }

  // https://stackoverflow.com/a/4731270
  def filePathToJpg(path: String): String =
    path.replaceAll("\\.[^.]*$", "") + jpgNameAddition

  def fileName(path: String): String =
    new File(path).getName
}