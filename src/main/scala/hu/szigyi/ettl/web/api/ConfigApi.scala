package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ConfigApi._
import hu.szigyi.ettl.web.app.WebApp.AppConfiguration
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

class ConfigApi(config: AppConfiguration) extends Http4sDsl[IO] with StrictLogging{

  val service = HttpRoutes.of[IO] {
    case GET -> Root => Ok(config)

      // TODO not supported yet -> not easy to change the AppConfig as it is wired to everywhere and have to maybe recreate ImageService as well
    case request@POST -> Root =>
      request.decode[ConfigRequest] { config =>
        InternalServerError()
      }
  }
}

object ConfigApi {
  implicit val appConfigurationCodec: Codec[AppConfiguration] = deriveCodec[AppConfiguration]
  implicit val configRequestCodec: Codec[ConfigRequest] = deriveCodec[ConfigRequest]

  case class ConfigRequest(rawDirectoryPath: String, logDirectoryPath: String)
}
