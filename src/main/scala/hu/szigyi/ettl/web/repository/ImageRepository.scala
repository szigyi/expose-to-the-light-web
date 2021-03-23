package hu.szigyi.ettl.web.repository

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.repository.ImageRepository.jpgPathStorage

class ImageRepository extends StrictLogging {
  def getPathOfAllImages: Seq[String] = jpgPathStorage
  def getPathOfLatestImage: Option[String] = jpgPathStorage.headOption
}

object ImageRepository {
  private var jpgPathStorage: Seq[String] = Seq.empty
  private var finishedSessionStorage: Seq[String] = Seq.empty

  def containsJpgPath(p: String): Boolean =
    finishedSessionStorage.contains(p) || jpgPathStorage.contains(p)

  def addJpgPath(p: String): Unit =
    if (!finishedSessionStorage.contains(p)) jpgPathStorage = jpgPathStorage :+ p

  def startNewSession: Unit = {
    finishedSessionStorage = finishedSessionStorage ++ jpgPathStorage
    jpgPathStorage = Seq.empty
  }
}
