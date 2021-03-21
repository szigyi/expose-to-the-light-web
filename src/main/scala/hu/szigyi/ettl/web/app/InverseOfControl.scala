package hu.szigyi.ettl.web.app

import cats.effect.{Blocker, ContextShift, IO}
import hu.szigyi.ettl.web.api.{ConfigApi, EttlApi, HealthApi, ImageApi, LogApi, StaticApi}
import hu.szigyi.ettl.web.service.{ConfigurationService, ImageService, LogService}

class InverseOfControl(env: String)(implicit cs: ContextShift[IO]) {
  private val blocker: Blocker = Blocker[IO].allocated.unsafeRunSync()._1

  private val configService = new ConfigurationService
  private val logService = new LogService(configService.logDirectoryPath)
  private val imageService = new ImageService(configService.rawDirectoryPath, configService.rawFileExtension)

  val staticApi = new StaticApi(blocker)
  val healthApi = new HealthApi(env)
  val configApi = new ConfigApi(configService)
  val logApi    = new LogApi(logService)
  val imageApi  = new ImageApi(blocker, imageService)
  val ettlApi   = new EttlApi(configService.rawDirectoryPath, configService.logDirectoryPath, configService.rawFileExtension)
}
