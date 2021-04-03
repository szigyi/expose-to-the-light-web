package hu.szigyi.ettl.web.api

import cats.effect.{Blocker, ContextShift, IO}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ImageApi.{ImageResponse, ImagesRequest}
import hu.szigyi.ettl.web.repository.ImageRepository
import hu.szigyi.ettl.web.service.ImageService
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent.{FileService, fileService}

class ImageApi(blocker: Blocker, imageService: ImageService, imgRepository: ImageRepository)(implicit cs: ContextShift[IO]) extends Http4sDsl[IO] with StrictLogging {

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case POST -> Root / "convert" =>
      Ok(ImageResponse(imgRepository.getPathOfLatestImage))

    case POST -> Root / "directories" =>
      Ok(imageService.getImageDirectories)

    case request @ POST -> Root =>
      request.decode[ImagesRequest] { imagesRequest =>
        Ok {
          val dirPaths = imageService.getImagePathsInDirectory(imagesRequest.directory).map(p => ImageResponse(Some(p)))
          if (imagesRequest.quickMode) dirPaths.takeRight(10) else dirPaths
        }
      }
  }

  // FIXME this is fishy, maybe security issue, but I haven't got better idea now
  val imageFileService: HttpRoutes[IO] =
    fileService[IO](FileService.Config("/", blocker))

}

object ImageApi {
  implicit val imagesRequestCodec: Codec[ImagesRequest] = deriveCodec[ImagesRequest]
  implicit val imageResponseCodec: Codec[ImageResponse] = deriveCodec[ImageResponse]

  case class ImagesRequest(directory: String, quickMode: Boolean)
  case class ImageResponse(latestImageName: Option[String])
}
