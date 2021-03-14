package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.util.Dir.getLatestFileInDirectory
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, fileName, filePathToJpg}

class ImageService(rawDirectoryPath: String, rawFileExtension: String) {

  private var convertedStorage: Set[String] = Set.empty

  def getFileNameOfLatestImage: Option[String] = {
    getLatestFileInDirectory(rawDirectoryPath, rawFileExtension).map { latestRaw =>
      val latestRawPath = latestRaw.getAbsolutePath
      val jpgPath       = filePathToJpg(latestRawPath)
      convertedStorage(jpgPath) match {
        case true =>
          fileName(jpgPath)
        case false =>
          convertToJpg(latestRawPath)
          convertedStorage = convertedStorage + jpgPath
          fileName(jpgPath)
      }
    }
  }
}
