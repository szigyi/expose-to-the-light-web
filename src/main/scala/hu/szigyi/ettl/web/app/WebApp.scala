package hu.szigyi.ettl.web.app

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.util.ManifestReader
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

// 1 make the log service to use timestamp and send the newest lines only
// 2 UI should only append the log lines
// 3 UI should poll every half a second the log lines
// 4 UI should use the latest timestamp from the returned log lines or now to poll
// 5 show the latest image on the UI
// TODO 6 create mini timelapse from the images on the UI :D - js based so raspberry pi has CPU to do other things
// TODO 7 filter log messages at server side based on log levels
// 8 as it is bugged and cannot download the jpg version of the image from the camera
//        compare the speed of exiftool (extract thumbnail and then resize with magick) and magick (raw to jpg)
//          Do I need a background process which does it or can I do it when image request comes in?
//          Which one is better for later use making gif?
// 9 figure out what is the easiest way to serve dynamically changing images from folder
// 10 make source folder of raw images a config or part of request
// 11 make image api more robust: can serve converted image if already exist; can serve latest image after page refresh
// 12 serve the entire last log file after page is refreshed not just incoming since now -> UI should have different states when first request goes to api and then just fetching latest
// 13 raw directory path and log path are coming from command line args and UI can get them -> makes the code more flexible and independent where it is running
// 14 can handle empty raw directory
// 15 read only known files types from folder
// 16 can start the ettl command line tool from the UI
// 17 responsive image on the UI
// 18 log's timestamp is separated and smaller then the message
// 19 newly added log lines should glow or should be obvious that was added recently
// 20 remove RAW file that is already converted
// 21 use customizable settings to run ettl from the UI
// 22 images and logs directory is driven from the UI when running ettl
// 23 add deploy, install and run scripts
// 24 read raw images from session's sub folder
// 25 raw, log path and raw extension can be stored in the url so user can bookmark it
// TODO 26 UI can show the time difference between captures took place - to know when the timelapse is ruined

object WebApp extends IOApp with StrictLogging {

  private val port = sys.env.getOrElse("http_port", "8230").toInt
  private val env  = sys.env.getOrElse("ENV", "local")

  private val threadPool = Executors.newFixedThreadPool(1)
  private val ec         = ExecutionContext.fromExecutor(threadPool)

  override def run(args: List[String]): IO[ExitCode] =
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

  private def httpApp(ioc: InverseOfControl): Kleisli[IO, Request[IO], Response[IO]] =
    Router(
      "/"        -> ioc.staticApi.service,
      "/health"  -> ioc.healthApi.service,
      "/config"  -> ioc.configApi.service,
      "/log"     -> ioc.logApi.service,
      "/convert" -> ioc.imageApi.convertService,
      "/image"   -> ioc.imageApi.imageFileService,
      "/images"  -> ioc.imageApi.allImagesService,
      "/ettl"    -> ioc.ettlApi.service,
    ).orNotFound

  private def banner(envName: String): String = {
    val manifestInfo = ManifestReader(getClass).manifestInfo()
    s"""
       |          _       _       _
       |  ___ ___| |_ ___| |_ ___| |
       | / -_)___|  _|___|  _|___| |
       | \\___|    \\__|    \\__|   |_| WEB
       |
       | http://localhost:8230/index.html
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
