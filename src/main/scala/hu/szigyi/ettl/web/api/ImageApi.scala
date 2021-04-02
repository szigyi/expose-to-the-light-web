package hu.szigyi.ettl.web.api

import cats.effect.{Blocker, ContextShift, IO}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ImageApi.ImageResponse
import hu.szigyi.ettl.web.repository.ImageRepository
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent.{FileService, fileService}

class ImageApi(blocker: Blocker, imgService: ImageRepository)(implicit cs: ContextShift[IO]) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case POST -> Root / "convert" =>
      Ok(ImageResponse(imgService.getPathOfLatestImage))

    case GET -> Root =>
      Ok(imgService.getPathOfAllImages.map(p => ImageResponse(Some(p))))
  }

  // FIXME this is fishy, maybe security issue, but I haven't got better idea now
  val imageFileService: HttpRoutes[IO] =
    fileService[IO](FileService.Config("/", blocker))

}

object ImageApi {
  implicit val imageResponseCodec: Codec[ImageResponse] = deriveCodec[ImageResponse]

  case class ImageResponse(latestImageName: Option[String])
}
