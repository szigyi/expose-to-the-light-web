package hu.szigyi.ettl.web.service

import cats.data.NonEmptyList
import hu.szigyi.ettl.web.util.ClosableOps._

import java.io.{File, FilenameFilter}
import java.nio.file.Paths
import scala.io.Source
import scala.util.Try

class DirectoryService {

  def getLatestFileInDirectory(dir: String, allowedExtension: String): Option[File] =
    filesInDirectory(new File(dir), Some(allowedExtension))
      .map(_.sortBy(_.getName).reverse.head)

  def getAllFilePathsInLatestSubDirectory(dir: String): Option[NonEmptyList[String]] =
    directoriesInDirectory(new File(dir))
      .map(_.sortBy(_.getName).reverse.head)
      .flatMap(latestSubDirectory =>
        filesInDirectory(latestSubDirectory, None)
          .map(_.sortBy(_.getName).reverse.map(_.getAbsolutePath)))

  def getAllFilePathsInDirectory(dir: String, allowedExtension: String): Option[NonEmptyList[String]] =
    filesInDirectory(new File(dir), Some(allowedExtension))
      .map(_.sortBy(_.getName).reverse.map(_.getAbsolutePath))

  def getDirectoriesInDirectory(dir: String): Option[NonEmptyList[String]] =
    directoriesInDirectory(new File(dir))
      .map(_.sortBy(_.getName).reverse.map(_.getAbsolutePath))

  def getPathFromParentDirectory(path: String): String = {
    val p = Paths.get(path)
    p.subpath(p.getNameCount - 2, p.getNameCount).toString
  }

  def deleteFile(path: String): Try[Unit] =
    Try(new File(path).delete())

  def getLinesOfFile(file: File): Seq[String] =
    withResources(Source.fromFile(file)) { source =>
      source.getLines().toSeq
    }

  private def directoriesInDirectory(d: File): Option[NonEmptyList[File]] =
    if (d.exists && d.isDirectory)
      NonEmptyList.fromList(d.listFiles().filter(_.isDirectory).toList)
    else
      None

  private def filesInDirectory(d: File, allowedExtension: Option[String]): Option[NonEmptyList[File]] =
    if (d.exists && d.isDirectory) {
      val files = allowedExtension match {
        case Some(ext) =>
          d.listFiles(allowedExtensionFilter(ext))
        case None =>
          d.listFiles()
      }
      NonEmptyList.fromList(files.filter(_.isFile).toList)
    } else
      None

  private def allowedExtensionFilter(extension: String): FilenameFilter =
    (_: File, name: String) => name.toLowerCase.endsWith(extension.toLowerCase)
}
