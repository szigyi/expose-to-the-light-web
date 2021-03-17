package hu.szigyi.ettl.web.app

import cats.effect.{Blocker, ContextShift, IO}
import hu.szigyi.ettl.web.api.{ConfigApi, EttlApi, HealthApi, ImageApi, LogApi, StaticApi}
import hu.szigyi.ettl.web.app.WebApp.AppConfiguration
import hu.szigyi.ettl.web.service.{ImageService, LogService}

class InverseOfControl(env: String, config: AppConfiguration)(implicit cs: ContextShift[IO]) {
  private val blocker: Blocker = Blocker[IO].allocated.unsafeRunSync()._1

  private val logService = new LogService(config.logDirectoryPath)
  private val imageService = new ImageService(config.rawDirectoryPath, config.rawFileExtension)

  val staticApi = new StaticApi(blocker)
  val healthApi = new HealthApi(env)
  val configApi = new ConfigApi(config)
  val logApi    = new LogApi(logService)
  val imageApi  = new ImageApi(blocker, config.rawDirectoryPath, imageService)
  val ettlApi   = new EttlApi()
}
