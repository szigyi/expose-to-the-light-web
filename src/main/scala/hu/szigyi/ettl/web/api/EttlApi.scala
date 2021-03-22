package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.EttlApi.EttlRequest
import hu.szigyi.ettl.web.service.ImageService
import hu.szigyi.ettl.web.util.EttlRunner
import org.http4s.circe.CirceEntityCodec._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class EttlApi(rawDirectoryPath: => Option[String], logDirectoryPath: => Option[String], rawFileExtension: => String) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request @ POST -> Root =>
      request.decode[EttlRequest] { req =>
        (logDirectoryPath, rawDirectoryPath) match {
          case (Some(logPath), Some(rawPath)) =>
            logger.info(s"Request: $req")
            ImageService.startNewSession
            Ok(EttlRunner.executeEttl(
              req.dummyCamera,
              req.setSettings,
              req.numberOfCaptures,
              req.intervalSeconds,
              rawFileExtension,
              logPath,
              rawPath
            ))
          case other =>
            logger.error(s"Cannot run Ettl. Missing configuration(s). rawDirectoryPath: $rawDirectoryPath, logDirectoryPath: $logDirectoryPath")
            InternalServerError()
        }
      }
  }
}

object EttlApi {
  implicit val ettlRequestCodec: Codec[EttlRequest] = deriveCodec[EttlRequest]

  case class EttlRequest(dummyCamera: Boolean,
                         setSettings: Boolean,
                         numberOfCaptures: Int,
                         intervalSeconds: Int)
}
