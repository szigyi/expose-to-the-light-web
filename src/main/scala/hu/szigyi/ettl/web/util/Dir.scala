package hu.szigyi.ettl.web.util

import cats.data.NonEmptyList

import java.io.{File, FilenameFilter}

object Dir {

  def filesInDirectory(dir: String, allowedExtension: String): Option[NonEmptyList[File]] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory)
      NonEmptyList.fromList(d.listFiles(allowedExtensionFilter(allowedExtension)).filter(_.isFile).toList)
    else
      None
  }

  def getLatestFileInDirectory(dir: String, allowedExtension: String): Option[File] =
    filesInDirectory(dir, allowedExtension).map(_.sortBy(_.getName).reverse.head)

  private def allowedExtensionFilter(extension: String): FilenameFilter =
    (_: File, name: String) => name.toLowerCase.endsWith(extension.toLowerCase)
}
