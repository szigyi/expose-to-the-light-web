package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.EttlApi.EttlRequest
import hu.szigyi.ettl.web.app.WebApp.AppConfiguration
import hu.szigyi.ettl.web.util.EttlRunner
import org.http4s.circe.CirceEntityCodec._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class EttlApi(appConfig: AppConfiguration) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request @ POST -> Root =>
      request.decode[EttlRequest] { req =>
      logger.info(s"Request: $req")
        Ok(EttlRunner.executeEttl(
          req.dummyCamera,
          req.setSettings,
          req.numberOfCaptures,
          req.intervalSeconds,
          req.rawFileExtension,
          appConfig.logDirectoryPath,
          appConfig.rawDirectoryPath
        ))
      }
  }
}

object EttlApi {
  implicit val ettlRequestCodec: Codec[EttlRequest] = deriveCodec[EttlRequest]

  case class EttlRequest(dummyCamera: Boolean,
                         setSettings: Boolean,
                         numberOfCaptures: Int,
                         intervalSeconds: Int,
                         rawFileExtension: String)
}
