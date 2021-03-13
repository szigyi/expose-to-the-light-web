package hu.szigyi.ettl.web.util

import cats.data.NonEmptyList

import java.io.{File, FilenameFilter}

object Dir {

  def filesInDirectory(dir: String, excludedFile: String): Option[NonEmptyList[File]] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory)
      NonEmptyList.fromList(d.listFiles(extensionFilter(excludedFile)).filter(_.isFile).toList)
    else
      None
  }

  def getLastNonJpgFileInDirectory(dir: String): Option[File] =
    filesInDirectory(dir, ".jpg").map(_.sortBy(_.getName).reverse.head)

  private def extensionFilter(extension: String): FilenameFilter =
    (_: File, name: String) => !name.toLowerCase.endsWith(extension)
}
