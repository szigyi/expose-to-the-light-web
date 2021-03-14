package hu.szigyi.ettl.web.service

import hu.szigyi.ettl.web.util.Dir.getLastNonJpgFileInDirectory
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, fileName, filePathToJpg}

class ImageService(rawDirectoryPath: String) {

  private var convertedStorage: Set[String] = Set.empty

  def getFileNameOfLatestImage: Option[String] = {
    getLastNonJpgFileInDirectory(rawDirectoryPath).map { latestRaw =>
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
