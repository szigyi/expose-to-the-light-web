package hu.szigyi.ettl.web.tool


import sys.process._
import scala.language.postfixOps
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.util.Timing.time


object MeasureRawToJpgConversion extends App with StrictLogging {

  val magickCommand =
    "convert /Users/szabolcs/Downloads/img/IMG_1.CR2 -resize 1080 -quality 75 /Users/szabolcs/Downloads/img/IMG_small_1.JPG"
  val exifCommand =
    "exiftool -b -PreviewImage /Users/szabolcs/Downloads/img/IMG_2.CR2 -w _interim.JPG"
  val exifMagickCommand =
    "convert /Users/szabolcs/Downloads/img/IMG_2_interim.JPG -resize 1080 -quality 75 /Users/szabolcs/Downloads/img/IMG_small_2.JPG"

  def execute(command: String): Int = command !

  logger.info("App is running and timing the commands...")
  time("Magick Only", execute(magickCommand))
  time("exifTool", execute(exifCommand))
  time("Magick after exifTool", execute(exifMagickCommand))
  /**
   * OUTPUT
   * Magick Only:              13117ms | 13117890686ns
   * exifTool:                   155ms | 155495345ns
   * Magick after exifTool:      559ms | 559523849ns
   * exifTool + Magick in total: 714ms
   *
   * Conclusion
   *
   * Using the exiftool to extract the embedded preview image and then use magick to decrease its size, quality is much, much faster
   * 714ms vs 13,117ms
   * then using the magick to read the entire raw file and convert it into the smaller sized jpg
   */
}
