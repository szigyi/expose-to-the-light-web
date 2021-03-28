package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.LogService.{LogLine, parseLogLine}

import java.time.format.DateTimeFormatter
import java.time.{LocalTime, ZoneOffset}
import scala.util.{Failure, Success, Try}

class LogService(dir: DirectoryService, logDirectoryPath: => Option[String]) extends StrictLogging {

  def readLatestLog: Seq[LogLine] = {
    logDirectoryPath match {
      case None => Seq.empty
      case Some(path) =>
        dir.getLatestFileInDirectory(path, ".log") match {
          case Some(latestLogFile) =>
            logger.trace(s"Reading log file: ${latestLogFile.toString}")
            dir
              .getLinesOfFile(latestLogFile)
              .flatMap(parseLogLine)
              .reverse
          case None =>
            Seq.empty
        }
    }
  }

  def readLogsSince(timestamp: LocalTime): Seq[LogLine] = {
    logger.trace(s"Reading log lines since: $timestamp")
    val ll = readLatestLog.filter(_.time.isAfter(timestamp))
    if (ll.nonEmpty) logger.debug(s"Read lines of log: ${ll.size} since: $timestamp")
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
        .withZone(ZoneOffset.UTC)
        .parse(ts))
      .map(LocalTime.from)
}
