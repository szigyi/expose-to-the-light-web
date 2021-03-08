package hu.szigyi.ettl.web.service

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.LogApi.{LogRequest, LogResponse}
import hu.szigyi.ettl.web.service.LogService.parseLogInstant

import java.io.File
import java.time.{Instant, ZoneOffset}
import java.time.format.DateTimeFormatter
import scala.io.Source
import scala.util.{Failure, Success, Try}
import hu.szigyi.ettl.web.util.ClosableOps._

class LogService extends StrictLogging {

  // TODO fetch logs after timestmap
  def readLogsSince(req: LogRequest): Seq[LogResponse] = {
    val logFile = filesInDirectory(req.path).sortBy(_.getName).reverse.head
    logger.trace(s"Reading log file: ${logFile.toString}")
    logger.trace(s"Reading log lines since: ${req.timestamp}")

    withResources(Source.fromFile(logFile)) { source =>
      val logs = source.getLines().toSeq.flatMap(line => {
        line.split(":::").toList match {
          case Nil =>
            logger.info("Empty file. No logs in the file.")
            None
          case timestampString :: message :: Nil => parseLogInstant(timestampString) match {
            case Success(timestamp) =>
              Some(LogResponse(timestamp, message))
            case Failure(exception) =>
              logger.error(s"Could not parse timestamp: $timestampString ::: $message")
              None
          }
          case other =>
            logger.error(s"Log file's structure does not match the expected: timestamp:::message != $other")
            None
        }
      })
      logs.reverse.filter(_.timestamp.isAfter(req.timestamp))
    }
  }

  private def filesInDirectory(dir: String): Seq[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toSeq
    } else {
      Seq[File]()
    }
  }

}

object LogService {
  def parseLogInstant(ts: String): Try[Instant] =
    Try(DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC)
      .parse(ts))
      .map(Instant.from)
}