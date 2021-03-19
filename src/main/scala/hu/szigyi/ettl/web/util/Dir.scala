package hu.szigyi.ettl.web.util

import cats.data.NonEmptyList

import java.io.{File, FilenameFilter}

object Dir {

  def getLatestFileInDirectory(dir: String, allowedExtension: String): Option[File] =
    filesInDirectory(new File(dir), allowedExtension)
      .map(_.sortBy(_.getName).reverse.head)

  def getLatestFileInLatestSubDirectory(dir: String, allowedExtension: String): Option[File] =
    directoriesInDirectory(new File(dir))
      .map(_.sortBy(_.getName).reverse.head)
      .flatMap(latestSubDirectory =>
        filesInDirectory(latestSubDirectory, allowedExtension)
          .map(_.sortBy(_.getName).reverse.head))

  private def directoriesInDirectory(d: File): Option[NonEmptyList[File]] =
    if (d.exists && d.isDirectory)
      NonEmptyList.fromList(d.listFiles().filter(_.isDirectory).toList)
    else
      None

  private def filesInDirectory(d: File, allowedExtension: String): Option[NonEmptyList[File]] =
    if (d.exists && d.isDirectory)
      NonEmptyList.fromList(d.listFiles(allowedExtensionFilter(allowedExtension)).filter(_.isFile).toList)
    else
      None

  private def allowedExtensionFilter(extension: String): FilenameFilter =
    (_: File, name: String) => name.toLowerCase.endsWith(extension.toLowerCase)
}
