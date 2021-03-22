package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.ConvertService.{Converter, Converting, Idle, converterRef}
import hu.szigyi.ettl.web.util.RawToJpg.convertToJpg

import java.util.concurrent.atomic.AtomicReference

class ConvertService(dir: DirectoryService, rawDirectoryPath: => Option[String], rawFileExtension: => String) extends StrictLogging {

  def run: Unit = {
    converterRef.get().state match {
      case Idle =>
        converterRef.set(Converter(Converting))
        convert
        converterRef.set(Converter(Idle))
      case Converting =>
        logger.debug(s"Conversion is in progress...")
    }
  }

  private def convert: Unit = {
    rawDirectoryPath.flatMap { rawPath =>
      dir.getAllFilePathsInLatestSubDirectory(rawPath).map { files =>
        val jpgs = files.toList.filter(_.endsWith("JPG"))
        val raws = files.toList.filter(_.endsWith(rawFileExtension))

        jpgs.foreach(jpg => if (!ImageService.containsJpgPath(jpg)) ImageService.addJpgPath(jpg))
        if (raws.nonEmpty && !"jpg".equalsIgnoreCase(rawFileExtension)) {
          if (convertToJpg(raws.last)) dir.deleteFile(raws.last)
        }
      }
    }
  }
}

object ConvertService {
  sealed trait ConversionState
  case object Converting extends ConversionState
  case object Idle       extends ConversionState
  case class Converter(state: ConversionState)

  private val converterRef = new AtomicReference(Converter(Idle))
}
