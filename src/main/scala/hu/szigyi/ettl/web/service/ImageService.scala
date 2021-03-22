package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.ImageService.jpgPathStorage

// track folder and give back only latest??
class ImageService extends StrictLogging {
  def getPathOfAllImages: Seq[String] = jpgPathStorage
  def getPathOfLatestImage: Option[String] = jpgPathStorage.headOption
}

object ImageService {
  private var jpgPathStorage: Seq[String] = Seq.empty

  def containsJpgPath(p: String): Boolean =
    jpgPathStorage.contains(p)

  def addJpgPath(p: String): Unit =
    jpgPathStorage = jpgPathStorage :+ p
}
