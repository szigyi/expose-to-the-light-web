package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.LogApi._
import hu.szigyi.ettl.web.service.LogService
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

import java.time.{Instant, LocalTime, ZoneId}

class LogApi(logService: LogService) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(logService.readLatestLogFile.map(LogResponse.apply))

    case request@POST -> Root =>
      request.decode[LogRequest] { logRequest =>
        Ok(logService.readLogsSince(
          logRequest.timestamp.atZone(ZoneId.systemDefault()).toLocalTime
        ).map(LogResponse.apply))
      }
  }
}

object LogApi {
  implicit val logRequestCodec: Codec[LogRequest] = deriveCodec[LogRequest]
  implicit val logResponseCodec: Codec[LogResponse] = deriveCodec[LogResponse]

  case class LogRequest(timestamp: Instant)

  case class LogResponse(timestamp: LocalTime, logLevel: String, message: String)
  object LogResponse {
    def apply(tup: (LocalTime, String, String)): LogResponse =
      LogResponse(tup._1, tup._2, tup._3)
  }

}