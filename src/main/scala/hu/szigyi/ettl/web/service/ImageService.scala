package hu.szigyi.ettl.web.service

class ImageService(dir: DirectoryService, rawDirectoryPath: => Option[String], rawFileExtension: => String) {
  def getImageDirectories: Seq[String] =
    rawDirectoryPath
      .flatMap(dir.getDirectoriesInDirectory)
      .map(_.toList)
      .getOrElse(Seq.empty)

  def getImagePathsInDirectory(baseDir: String): Seq[String] =
    dir.getAllFilePathsInDirectory(baseDir, rawFileExtension)
      .map(_.toList)
      .getOrElse(Seq.empty)
}