package hu.szigyi.ettl.web.util

import java.io.File

object Dir {

  def filesInDirectory(dir: String): Seq[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).toSeq
    else
      Seq[File]()
  }

  def getLastFileInDirectory(dir: String): File =
    filesInDirectory(dir).sortBy(_.getName).reverse.head
}
