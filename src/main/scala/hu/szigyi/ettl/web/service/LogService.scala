package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.LogService.{LogLine, parseLogLine}

import java.time.format.DateTimeFormatter
import java.time.{LocalTime, ZoneId}
import scala.util.{Failure, Success, Try}

class LogService(dir: DirectoryService, logDirectoryPath: => Option[String]) extends StrictLogging {

  def readLatestLog: Seq[LogLine] = {
    logDirectoryPath match {
      case None =>
        logger.trace(s"Log Directory Path is not provided")
        Seq.empty
      case Some(path) =>
        dir.getLatestFileInDirectory(path, ".log") match {
          case Some(latestLogFile) =>
            logger.trace(s"Reading log file: ${latestLogFile.toString}")
            dir
              .getLinesOfFile(latestLogFile)
              .flatMap(parseLogLine)
              .sortBy(_.time)
              .reverse
          case None =>
            logger.trace(s"Not found latest log file in the log directory")
            Seq.empty
        }
    }
  }

  def readLogsSince(since: LocalTime): Seq[LogLine] = {
    logger.trace(s"Reading log lines since: $since")
    val ll = readLatestLog.filter(_.time.isAfter(since))
    if (ll.nonEmpty) logger.debug(s"Read lines of log: ${ll.size} since: $since")
    ll
  }
}

object LogService extends StrictLogging {
  case class LogLine(time: LocalTime, level: String, message: String)

  def parseLogLine(line: String): Option[LogLine] =
    line.split(":::").toList match {
      case Nil =>
        logger.info("Empty file. No logs in the file.")
        None
      case timestampString :: logLevel :: message :: Nil =>
        parseLocalTime(timestampString) match {
          case Success(timestamp) =>
            Some(LogLine(timestamp, logLevel, message))
          case Failure(exception) =>
            logger.error(s"Could not parse timestamp: $timestampString ::: $logLevel ::: $message", exception)
            None
        }
      case other =>
        logger.error(s"Log file's structure does not match the expected: timestamp:::logLevel:::message != $other")
        None
    }

  def parseLocalTime(ts: String): Try[LocalTime] =
    Try(
      DateTimeFormatter
        .ofPattern("HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())
        .parse(ts))
      .map(LocalTime.from)
}
