package hu.szigyi.ettl.web.app

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.tool.ManifestReader
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object WebApp extends IOApp with StrictLogging {

  private val port = sys.env.getOrElse("http_port", "8230").toInt
  private val env = sys.env.getOrElse("ENV", "local")

  private val threadPool = Executors.newFixedThreadPool(1)
  private val ec = ExecutionContext.fromExecutor(threadPool)

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](ec)
      .bindHttp(port, "0.0.0.0")
      .withBanner(Seq(banner(env)))
      .withHttpApp(httpApp(new InverseOfControl(env)))
      .serve
      .compile
      .drain
      .handleErrorWith(logErrorAsAFinalFrontier)
      .map(_ => threadPool.shutdown())
      .map(_ => finalWords())
      .as(ExitCode.Success)
  }

  private def httpApp(ioc: InverseOfControl): Kleisli[IO, Request[IO], Response[IO]] =
    Router(
      "/"       -> ioc.staticApi.service,
      "/health" -> ioc.healthApi.service,
      "/log"    -> ioc.logApi.service
    ).orNotFound

  private def banner(envName: String): String = {
    val manifestInfo = ManifestReader(getClass).manifestInfo()
    s"""
       |          _       _       _
       |  ___ ___| |_ ___| |_ ___| |
       | / -_)___|  _|___|  _|___| |
       | \\___|    \\__|    \\__|   |_|
       |
       | Build Number: ${manifestInfo.buildNumber}
       | Build Time:   ${manifestInfo.buildTimeStamp}
       | Git Hash:     ${manifestInfo.gitHash}
       | ENV:          ${envName.toUpperCase}""".stripMargin
  }

  private def logErrorAsAFinalFrontier(throwable: Throwable): IO[Unit] = {
    logger.error(s"App is terminating because of ${throwable.getMessage}")
    IO.raiseError(throwable)
  }

  private def finalWords(): Unit =
    logger.info(s"App is terminating as you said so!")

}
