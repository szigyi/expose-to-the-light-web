package hu.szigyi.ettl.web.job

import cats.effect.{IO, Timer}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration.FiniteDuration

object Job extends StrictLogging {
  def streamingTask(taskToExec: => Unit, rate: FiniteDuration)(implicit timer: Timer[IO]): fs2.Stream[IO, Unit] =
    fs2.Stream
      .eval(protectStreamFromIOError(taskToExec)) ++ fs2.Stream
      .repeatEval(protectStreamFromIOError(taskToExec))
      .metered(rate)

  private def protectStreamFromIOError(taskToExec: => Unit): IO[Unit] =
    IO(taskToExec).handleErrorWith(exception => {
      IO.unit.map(
        _ =>
          logger.error(
            s"Critical error happened. IO task has failed, but we are throwing away this error in order to keep the app's main fs2.Stream healthy, so other tasks can run!",
            exception
        ))
    })
}
