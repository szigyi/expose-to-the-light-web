package hu.szigyi.ettl.web.api

import cats.effect.{Blocker, ContextShift, IO}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.ImageApi.{ImageRequest, ImageResponse}
import hu.szigyi.ettl.web.util.Dir.getLastNonJpgFileInDirectory
import hu.szigyi.ettl.web.util.RawToJpg.{convertToJpg, fileName, filePathToJpg}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent.{FileService, fileService}

class ImageApi(blocker: Blocker)(implicit cs: ContextShift[IO]) extends Http4sDsl[IO] with StrictLogging {

  var convertedStorage: Set[String] = Set.empty

  val convertService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request @ POST -> Root =>
      request.decode[ImageRequest] { req =>
        getLastNonJpgFileInDirectory(req.rawDirectoryPath) match {
          case Some(latestRaw) =>
            val latestRawPath = latestRaw.getAbsolutePath
            val jpgPath       = filePathToJpg(latestRawPath)
            convertedStorage.find(_ == jpgPath) match {
              case Some(_) =>
                Ok(ImageResponse(fileName(jpgPath)))
              case None =>
                convertToJpg(latestRawPath)
                convertedStorage = convertedStorage + jpgPath
                Ok(ImageResponse(fileName(jpgPath)))
            }
          case None => NotFound()
        }
      }
  }

  val imageService: HttpRoutes[IO] = fileService[IO](FileService.Config("/Users/szabolcs/Downloads/img", blocker))
}

object ImageApi {
  implicit val imageRequestCodec: Codec[ImageRequest]   = deriveCodec[ImageRequest]
  implicit val imageResponseCodec: Codec[ImageResponse] = deriveCodec[ImageResponse]

  case class ImageRequest(rawDirectoryPath: String)
  case class ImageResponse(latestImageName: String)
}
