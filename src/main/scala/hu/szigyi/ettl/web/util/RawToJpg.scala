package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

object RawToJpg extends StrictLogging {
  val jpgNameAddition: String = ".JPG"

  def convertToJpg(rawPath: String): Boolean = {
    val jpgPath = filePathToJpg(rawPath)
    logger.debug(s"converting raw '$rawPath' to '$jpgPath'...")
    val exifCommand =
      s"exiftool -b -PreviewImage $rawPath -w $jpgNameAddition"
    val magickCommand =
      s"convert $jpgPath -resize 1080 -quality 75 $jpgPath"

    val exifResult = exifCommand !

    val magickResult = magickCommand !

    (exifResult == 0) && (magickResult == 0)
  }

  // https://stackoverflow.com/a/4731270
  def filePathToJpg(path: String): String =
    path.replaceAll("\\.[^.]*$", "") + jpgNameAddition
}