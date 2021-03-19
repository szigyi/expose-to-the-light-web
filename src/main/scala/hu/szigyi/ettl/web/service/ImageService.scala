package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.util.Dir.getLatestFileInLatestSubDirectory
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, getPathFromParentDirectory, filePathToJpg}

class ImageService(rawDirectoryPath: String, rawFileExtension: String) {

  private var convertedStorage: Set[String] = Set.empty

  def getRelativePathOfLatestImage: Option[String] = {
    getLatestFileInLatestSubDirectory(rawDirectoryPath, rawFileExtension).map { latestRaw =>
      val latestRawPath = latestRaw.getAbsolutePath
      val jpgPath       = filePathToJpg(latestRawPath)
      convertedStorage(jpgPath) match {
        case true =>
          getPathFromParentDirectory(jpgPath)
        case false =>
          convertToJpg(latestRawPath)
          convertedStorage = convertedStorage + jpgPath
          getPathFromParentDirectory(jpgPath)
      }
    }
  }
}
