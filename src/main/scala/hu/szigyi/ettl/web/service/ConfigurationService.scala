package hu.szigyi.ettl.web.service

class ConfigurationService {
  private var raw: Option[String] = None
  private var log: Option[String] = None
  private var ext: String = "JPG"
  private var level: String = "INFO"

  def rawDirectoryPath: Option[String] = raw
  def logDirectoryPath: Option[String] = log
  def rawFileExtension: String = ext
  def logLevel: String = level

  def setRawDirectoryPath(r: Option[String]) =
    raw = r
  def setLogDirectoryPath(l: Option[String]) =
    log = l
  def setRawFileExtension(e: String) =
    ext = e
  def setLogLevel(l: String) =
    level = l
}
