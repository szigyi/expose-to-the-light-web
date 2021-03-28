package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.LogApi._
import hu.szigyi.ettl.web.service.LogService
import hu.szigyi.ettl.web.service.LogService.LogLine
import hu.szigyi.ettl.web.util.LocalTimeUtil.translateTimeToAnotherZone
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

import java.time.{Instant, LocalTime, ZoneId}

class LogApi(logService: LogService) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(logService.readLatestLog.map(LogResponse.apply))

    case request @ POST -> Root =>
      request.decode[LogRequest] { logRequest =>
        Ok {
          logService
            .readLogsSince(
              logRequest.since.atZone(ZoneId.systemDefault()).toLocalTime
            )
            .map(LogResponse.apply)
        }
      }
  }
}

object LogApi {
  implicit val logRequestCodec: Codec[LogRequest]   = deriveCodec[LogRequest]
  implicit val logResponseCodec: Codec[LogResponse] = deriveCodec[LogResponse]

  case class LogRequest(since: Instant)

  // timestamp should be in UTC as UI not supporting proper local time right now
  case class LogResponse(timestamp: LocalTime, logLevel: String, message: String)
  object LogResponse {
    def apply(log: LogLine): LogResponse =
      LogResponse(translateTimeToAnotherZone(log.time, ZoneId.systemDefault(), ZoneId.of("UTC")), log.level, log.message)
  }
}
