package hu.szigyi.ettl.web.app

import cats.effect.{Blocker, ContextShift, IO}
import hu.szigyi.ettl.web.api.{HealthApi, StaticApi}

class InverseOfControl(env: String)(implicit cs: ContextShift[IO]) {
  private val blocker: Blocker = Blocker[IO].allocated.unsafeRunSync()._1

  val staticApi = new StaticApi(blocker)
  val healthApi = new HealthApi(env)
}
