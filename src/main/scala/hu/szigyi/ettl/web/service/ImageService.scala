package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, filePathToJpg}

class ImageService(dir: DirectoryService,
                   rawDirectoryPath: => Option[String],
                   rawFileExtension: => String) extends StrictLogging {

  private var convertedStorage: Set[String] = Set.empty

  def getPathOfLatestImage: Option[String] =
    rawDirectoryPath.flatMap { path =>
      dir.getLatestFileInLatestSubDirectory(path, rawFileExtension).map { latestRaw =>
        val latestRawPath = latestRaw.getAbsolutePath
        val jpgPath       = filePathToJpg(latestRawPath)
        if (convertedStorage(jpgPath)) {
          jpgPath
        } else {
          if (convertToJpg(latestRawPath)) {
            logger.info(s"Deleting RAW image: $latestRawPath")
            dir.deleteFile(latestRawPath)
          }
          convertedStorage = convertedStorage + jpgPath
          jpgPath
        }
      }
    }
}
