package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ConfigApi._
import hu.szigyi.ettl.web.service.ConfigurationService
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

class ConfigApi(configService: ConfigurationService) extends Http4sDsl[IO] with StrictLogging{

  val service = HttpRoutes.of[IO] {
    case request@POST -> Root =>
      request.decode[ConfigRequest] { config =>
      logger.info(s"Changing config to: $config")
        configService.setRawDirectoryPath(Some(config.rawDirectoryPath))
        configService.setLogDirectoryPath(Some(config.logDirectoryPath))
        configService.setRawFileExtension(config.rawFileExtension)
        Ok()
      }
  }
}

object ConfigApi {
  implicit val configRequestCodec: Codec[ConfigRequest] = deriveCodec[ConfigRequest]

  case class ConfigRequest(rawDirectoryPath: String, logDirectoryPath: String, rawFileExtension: String)
}
