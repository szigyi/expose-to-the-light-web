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

import java.time.Instant

class LogApi(logService: LogService) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request@POST -> Root =>
      request.decode[LogRequest] { logRequest =>
        Ok(logService.readLogsSince(logRequest))
      }
  }
}

object LogApi {
  implicit val logRequestCodec: Codec[LogRequest] = deriveCodec[LogRequest]
  implicit val logResponseCodec: Codec[LogResponse] = deriveCodec[LogResponse]

  case class LogRequest(timestamp: Instant, path: String)

  case class LogResponse(timestamp: Instant, message: String)

}