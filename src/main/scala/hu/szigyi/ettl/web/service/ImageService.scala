package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.util.Dir.{deleteFile, getLatestFileInLatestSubDirectory, getPathFromParentDirectory}
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, filePathToJpg}

class ImageService(rawDirectoryPath: => Option[String], rawFileExtension: => String) extends StrictLogging {

  private var convertedStorage: Set[String] = Set.empty

  def getPathOfLatestImage: Option[String] =
    rawDirectoryPath.flatMap { path =>
      getLatestFileInLatestSubDirectory(path, rawFileExtension).map { latestRaw =>
        val latestRawPath = latestRaw.getAbsolutePath
        val jpgPath       = filePathToJpg(latestRawPath)
        convertedStorage(jpgPath) match {
          case true =>
            jpgPath
          case false =>
            if (convertToJpg(latestRawPath)) {
              logger.info(s"Deleting RAW image: $latestRawPath")
              deleteFile(latestRawPath)
            }
            convertedStorage = convertedStorage + jpgPath
            jpgPath
        }
      }
    }
}
