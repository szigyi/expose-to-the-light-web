package hu.szigyi.ettl.web.app

import cats.effect.{Blocker, ContextShift, IO, Timer}
import hu.szigyi.ettl.web.api.{ConfigApi, EttlApi, HealthApi, ImageApi, LogApi, StaticApi}
import hu.szigyi.ettl.web.job.Job
import hu.szigyi.ettl.web.repository.ImageService
import hu.szigyi.ettl.web.service.{ConfigurationService, ConvertService, DirectoryService, LogService}

import scala.concurrent.duration._

class InverseOfControl(env: String)(implicit cs: ContextShift[IO], timer: Timer[IO]) {
  private val blocker: Blocker = Blocker[IO].allocated.unsafeRunSync()._1

  private val configService    = new ConfigurationService
  private val directoryService = new DirectoryService
  private val logService       = new LogService(directoryService, configService.logDirectoryPath)
  private val imageService     = new ImageService
  private val convertService           = new ConvertService(directoryService, configService.rawDirectoryPath, configService.rawFileExtension)

  val staticApi = new StaticApi(blocker)
  val healthApi = new HealthApi(env)
  val configApi = new ConfigApi(configService)
  val logApi    = new LogApi(logService)
  val imageApi  = new ImageApi(blocker, imageService)
  val ettlApi   = new EttlApi(configService.rawDirectoryPath, configService.logDirectoryPath, configService.rawFileExtension)

  val convertJob = Job.streamingTask(convertService.run, 100.milliseconds)
}
