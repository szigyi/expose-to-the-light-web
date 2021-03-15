package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.LogService.parseLocalDate
import hu.szigyi.ettl.web.util.ClosableOps._
import hu.szigyi.ettl.web.util.Dir.getLatestFileInDirectory

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalTime, ZoneOffset}
import scala.io.Source
import scala.util.{Failure, Success, Try}

class LogService(logDirectoryPath: String) extends StrictLogging {

  def readLatestLogFile: Seq[(LocalTime, String)] =
    getLatestFileInDirectory(logDirectoryPath, ".log") match {
      case Some(latestLogFile) =>
        logger.trace(s"Reading log file: ${latestLogFile.toString}")

        withResources(Source.fromFile(latestLogFile)) { source =>
          val logs = source.getLines().toSeq.flatMap(line => {
            line.split(":::").toList match {
              case Nil =>
                logger.info("Empty file. No logs in the file.")
                None
              case timestampString :: message :: Nil => parseLocalDate(timestampString) match {
                case Success(timestamp) =>
                  Some((timestamp, message))
                case Failure(exception) =>
                  logger.error(s"Could not parse timestamp: $timestampString ::: $message")
                  None
              }
              case other =>
                logger.error(s"Log file's structure does not match the expected: timestamp:::message != $other")
                None
            }
          })
          logs.reverse
        }
      case None =>
        Seq.empty
    }

  def readLogsSince(timestamp: LocalTime): Seq[(LocalTime, String)] = {
    logger.trace(s"Reading log lines since: $timestamp")
    readLatestLogFile.filter(_._1.isAfter(timestamp))
  }
}

object LogService {
  def parseLocalDate(ts: String): Try[LocalTime] =
    Try(DateTimeFormatter
      .ofPattern("HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC)
      .parse(ts))
      .map(LocalTime.from)
}