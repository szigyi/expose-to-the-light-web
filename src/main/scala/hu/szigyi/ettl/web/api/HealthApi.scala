package hu.szigyi.ettl.web.api

import cats.effect.IO
import hu.szigyi.ettl.web.api.HealthApi.HealthModel
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

import java.time.Instant

class HealthApi(env: String) extends Http4sDsl[IO] {

  val service = HttpRoutes.of[IO] {
    case GET -> Root => Ok(HealthModel(Instant.now(), env, false))
  }
}

object HealthApi {
  case class HealthModel(time: Instant, env: String, running: Boolean)
}

