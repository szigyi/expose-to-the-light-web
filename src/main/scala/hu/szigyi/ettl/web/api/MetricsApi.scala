package hu.szigyi.ettl.web.api

import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.api.MetricsApi.{TimeResidualRequest, TimeResidualResponse}
import hu.szigyi.ettl.web.repository.MetricsRepository.TimeResidualDomain
import hu.szigyi.ettl.web.service.MetricsService
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

import java.time.{Duration, Instant, LocalTime}

class MetricsApi(metricsService: MetricsService) extends Http4sDsl[IO] with StrictLogging {
  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok {
        metricsService.getLatestTimeResiduals
          .map(TimeResidualResponse.apply)
      }

    case request @ POST -> Root =>
      request.decode[TimeResidualRequest] { req =>
        Ok {
          metricsService
            .getLatestTimeResidualsSince(req.since)
            .map(TimeResidualResponse.apply)
        }
      }
  }
}

object MetricsApi {
  implicit val timeResidualRequestCodec: Decoder[TimeResidualRequest]   = deriveDecoder[TimeResidualRequest]
  implicit val durationCodec: Encoder[Duration]                         = d => Json.fromLong(d.toMillis)
  implicit val timeResidualResponseCodec: Encoder[TimeResidualResponse] = deriveEncoder[TimeResidualResponse]

  case class TimeResidualRequest(since: Instant)

  case class TimeResidualResponse(orderNumber: Int, difference: Duration, actual: LocalTime, expected: LocalTime)
  object TimeResidualResponse {
    def apply(t: TimeResidualDomain): TimeResidualResponse =
      TimeResidualResponse(t.orderNumber, Duration.ofMillis(t.difference.toMillis), t.actual, t.expected)
  }
}
