package hu.szigyi.ettl.web.util

import scala.util.control.NonFatal

/**
 * CREDIT: https://github.com/dkomanov/stuff/blob/master/src/com/komanov/io/package.scala
 */
object ClosableOps {

  def withResources[T <: AutoCloseable, V](r: => T)(f: T => V): V = {
    val resource: T = r
    require(resource != null, "resource is null")
    var exception: Throwable = null
    try {
      f(resource)
    } catch {
      case e: Throwable =>
        exception = e
        throw e
    } finally {
      closeAndAddSuppressed(exception, resource)
    }
  }

  private def closeAndAddSuppressed(e: Throwable, resource: AutoCloseable): Unit = {
    if (e != null) {
      try {
        resource.close()
      } catch {
        case NonFatal(suppressed) =>
          e.addSuppressed(suppressed)
        case fatal: Throwable if NonFatal(e) =>
          fatal.addSuppressed(e)
          throw fatal
        case fatal: InterruptedException =>
          fatal.addSuppressed(e)
          throw fatal
        case fatal: Throwable =>
          e.addSuppressed(fatal)
      }
    } else {
      resource.close()
    }
  }
}
