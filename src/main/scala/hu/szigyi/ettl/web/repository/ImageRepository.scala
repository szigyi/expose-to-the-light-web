package hu.szigyi.ettl.web.repository

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.repository.ImageRepository.{finishedSessionStorage, jpgPathStorage}

class ImageRepository extends StrictLogging {
  def getPathOfAllImages: Seq[String] = {
    logger.trace(s"Size of the image storage ${jpgPathStorage.size}")
    jpgPathStorage
  }
  def getPathOfLatestImage: Option[String] = jpgPathStorage.headOption

  def startNewSession(): Unit = {
    logger.debug(s"Starting new session in image storage. Archiving ${jpgPathStorage.size} images.")
    finishedSessionStorage = finishedSessionStorage ++ jpgPathStorage
    jpgPathStorage = Seq.empty
  }

  def containsJpgPath(p: String): Boolean =
    finishedSessionStorage.contains(p) || jpgPathStorage.contains(p)

  def addJpgPath(p: String): Unit = {
    logger.trace(s"Trying to add to image storage: $p")
    if (!finishedSessionStorage.contains(p)) {
      logger.debug(s"Adding to image storage: $p")
      jpgPathStorage = (jpgPathStorage :+ p).sorted.reverse
    }
  }
}

object ImageRepository {
  private var jpgPathStorage: Seq[String] = Seq.empty
  private var finishedSessionStorage: Seq[String] = Seq.empty
}
