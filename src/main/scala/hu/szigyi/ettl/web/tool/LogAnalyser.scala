package hu.szigyi.ettl.web.tool

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.{LogService, MetricsService}
import hu.szigyi.ettl.web.service.LogService.LogLine

import java.time.temporal.ChronoUnit._

object LogAnalyser extends App with StrictLogging {

  val service = new MetricsService(new LogService(null, null) {
    override def readLatestLog: Seq[LogLine] = stringToLogLines(log).reverse
  })

  def stringToLogLines(logs: String): Seq[LogLine] = {
    val logLinesS: Seq[String] = logs.split("\n").toSeq.map(_.trim).filter(_ != "")
    logger.info(s"logLinesS: ${logLinesS.size}")

    val logLines: Seq[LogLine] = logLinesS.flatMap(LogService.parseLogLine)
    logger.info(s"logLines: ${logLines.size}")
    logLines
  }

  def timesBetweenShots(lines: Seq[LogLine]): Seq[String] =
    lines.filter(_.message.contains("Taking photo")).sliding(2).toSeq.zipWithIndex.map {
      case (window, index) => {
        val prev          = window(0)
        val current       = window(1)
        val elapsedMillis = MILLIS.between(prev.time, current.time)
        val value         = s"$elapsedMillis millis \t between ${index + 1}-${index + 2}"
        logger.info(value)
        value
      }
    }

  val log =
    """
19:45:40.354:::I:::             Clock: SystemClock[Europe/London]
19:45:40.368:::I:::      Dummy Camera: true
19:45:40.369:::I:::  Images Base Path: /home/pi/dev/ettl/captured-images
19:45:40.370:::I:::     # of Captures: 10
19:45:40.371:::I:::      Set Settings: false
19:45:40.371:::I:::          Interval: 2 seconds
19:45:40.372:::I:::Raw File Extension: JPG
19:45:40.617:::I:::Connecting to camera...
19:45:42.167:::I:::[1/10] Taking photo...
19:45:42.178:::D:::[1/10] Capture took: 5ms | 5823396ns
19:45:43.625:::I:::[1/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0001.JPG
19:45:43.626:::D:::[1/10] Schedule took: 1486ms | 1486558004ns
19:45:43.661:::I:::[2/10] Taking photo...
19:45:43.662:::D:::[2/10] Capture took: 0ms | 19843ns
19:45:43.691:::I:::[2/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0002.JPG
19:45:43.693:::D:::[2/10] Schedule took: 50ms | 50802399ns
19:45:44.696:::I:::[3/10] Taking photo...
19:45:44.698:::D:::[3/10] Capture took: 0ms | 30624ns
19:45:44.729:::I:::[3/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0003.JPG
19:45:44.730:::D:::[3/10] Schedule took: 1036ms | 1036416732ns
19:45:46.736:::I:::[4/10] Taking photo...
19:45:46.738:::D:::[4/10] Capture took: 0ms | 31406ns
19:45:46.768:::I:::[4/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0004.JPG
19:45:46.769:::D:::[4/10] Schedule took: 2037ms | 2037191478ns
19:45:48.686:::I:::[5/10] Taking photo...
19:45:48.689:::D:::[5/10] Capture took: 0ms | 35365ns
19:45:48.717:::I:::[5/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0005.JPG
19:45:48.719:::D:::[5/10] Schedule took: 1948ms | 1948025863ns
19:45:50.726:::I:::[6/10] Taking photo...
19:45:50.728:::D:::[6/10] Capture took: 0ms | 31562ns
19:45:50.755:::I:::[6/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0006.JPG
19:45:50.757:::D:::[6/10] Schedule took: 2036ms | 2036269714ns
19:45:52.663:::I:::[7/10] Taking photo...
19:45:52.665:::D:::[7/10] Capture took: 0ms | 33124ns
19:45:52.696:::I:::[7/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0007.JPG
19:45:52.698:::D:::[7/10] Schedule took: 1940ms | 1940070815ns
19:45:54.705:::I:::[8/10] Taking photo...
19:45:54.706:::D:::[8/10] Capture took: 0ms | 21250ns
19:45:54.720:::I:::[8/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0008.JPG
19:45:54.721:::D:::[8/10] Schedule took: 2020ms | 2020903625ns
19:45:56.727:::I:::[9/10] Taking photo...
19:45:56.728:::D:::[9/10] Capture took: 0ms | 31302ns
19:45:56.759:::I:::[9/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0009.JPG
19:45:56.760:::D:::[9/10] Schedule took: 2038ms | 2038925528ns
19:45:58.673:::I:::[10/10] Taking photo...
19:45:58.674:::D:::[10/10] Capture took: 0ms | 20729ns
19:45:58.698:::I:::[10/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_24_19_45_40/IMG_0010.JPG
19:45:58.699:::D:::[10/10] Schedule took: 1930ms | 1930740309ns
19:45:58.847:::I:::App finished
      """

  private val logLines = stringToLogLines(log)
  timesBetweenShots(logLines)
  logger.info("Time Residuals")
  service.getLatestTimeResiduals.map(r => logger.info(s"${r.orderNumber}\t${r.difference}\t${r.expected}-${r.actual}"))
}
