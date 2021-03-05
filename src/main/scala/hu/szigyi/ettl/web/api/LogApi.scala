package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.LogApi._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

import java.time.Instant

class LogApi extends Http4sDsl[IO] with StrictLogging {

  val service = HttpRoutes.of[IO] {
    case request@POST -> Root =>
      logger.info(request.bodyText.compile.string.unsafeRunSync())
      request.decode[LogRequest] { logRequest =>
        Ok(Seq(
          LogResponse(Instant.now(), "First line"),
          LogResponse(Instant.now(), "Second line")
        ))
      }.redeemWith(t => {
        logger.error("ERR", t)
        InternalServerError()
      }, r => IO(r))
  }
}

object LogApi {
  implicit val logRequestCodec: Codec[LogRequest] = deriveCodec[LogRequest]
  implicit val logResponseCodec: Codec[LogResponse] = deriveCodec[LogResponse]

//  implicit val logRequestEntityDecoder: EntityDecoder[IO, LogRequest] = circeEntityDecoder

  case class LogRequest(timestamp: String, path: String)

  case class LogResponse(timestamp: Instant, message: String)

}