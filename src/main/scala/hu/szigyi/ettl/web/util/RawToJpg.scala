package hu.szigyi.ettl.web.util

import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging

import java.nio.file.Paths

object RawToJpg extends StrictLogging {
  val jpgNameAddition: String = ".JPG"

  def convertToJpg(rawPath: String) = {
    val jpgPath = filePathToJpg(rawPath)
    logger.debug(s"converting raw '$rawPath' to '$jpgPath'...")
    val exifCommand =
      s"exiftool -b -PreviewImage $rawPath -w $jpgNameAddition"
    val magickCommand =
      s"magick convert $jpgPath -resize 1080 -quality 75 $jpgPath"

    exifCommand !

    magickCommand !
  }

  // https://stackoverflow.com/a/4731270
  def filePathToJpg(path: String): String =
    path.replaceAll("\\.[^.]*$", "") + jpgNameAddition

  def getPathFromParentDirectory(path: String): String = {
    val p = Paths.get(path)
    p.subpath(p.getNameCount - 2, p.getNameCount).toString
  }
}