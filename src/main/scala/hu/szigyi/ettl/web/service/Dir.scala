package hu.szigyi.ettl.web.service

import cats.data.NonEmptyList

import java.io.{File, FilenameFilter}
import java.nio.file.Paths
import scala.util.Try

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

  def getPathFromParentDirectory(path: String): String = {
    val p = Paths.get(path)
    p.subpath(p.getNameCount - 2, p.getNameCount).toString
  }

  def deleteFile(path: String): Try[Unit] =
    Try(new File(path).delete())

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
