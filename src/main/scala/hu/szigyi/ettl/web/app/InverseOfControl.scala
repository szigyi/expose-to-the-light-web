package hu.szigyi.ettl.web.app

import cats.effect.{Blocker, ContextShift, IO}
import hu.szigyi.ettl.web.api.{HealthApi, LogApi, StaticApi}
import hu.szigyi.ettl.web.service.LogService

class InverseOfControl(env: String)(implicit cs: ContextShift[IO]) {
  private val blocker: Blocker = Blocker[IO].allocated.unsafeRunSync()._1

  private val logService = new LogService()

  val staticApi = new StaticApi(blocker)
  val healthApi = new HealthApi(env)
  val logApi    = new LogApi(logService)
}
