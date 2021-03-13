package hu.szigyi.ettl.web.api

import cats.effect.{Blocker, ContextShift, IO}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ImageApi.{ImageRequest, ImageResponse}
import hu.szigyi.ettl.web.util.Dir.getLastFileInDirectory
import hu.szigyi.ettl.web.util.RawToJpg.{convert, fileNameToJpg}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent.{FileService, fileService}


class ImageApi(blocker: Blocker)(implicit cs: ContextShift[IO]) extends Http4sDsl[IO] with StrictLogging{

  val convertService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request@POST -> Root =>
      request.decode[ImageRequest] { req =>
        val latestImageFile = getLastFileInDirectory(req.path).getAbsolutePath
        val jpgVersion = fileNameToJpg(latestImageFile)
        convert(latestImageFile, jpgVersion)
        Ok(ImageResponse(jpgVersion))
      }
  }

  val imageService = fileService[IO](FileService.Config("/Users/szabolcs/Downloads/img", blocker))
}

object ImageApi {
  implicit val imageRequestCodec: Codec[ImageRequest] = deriveCodec[ImageRequest]
  implicit val imageResponseCodec: Codec[ImageResponse] = deriveCodec[ImageResponse]

  case class ImageRequest(path: String)
  case class ImageResponse(path: String)
}
