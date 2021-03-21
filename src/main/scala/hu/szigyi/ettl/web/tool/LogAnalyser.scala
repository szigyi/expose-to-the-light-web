package hu.szigyi.ettl.web.tool

import com.typesafe.scalalogging.StrictLogging
import hu.szigyi.ettl.web.service.LogService
import hu.szigyi.ettl.web.service.LogService.LogLine

import java.time.temporal.ChronoUnit._

object LogAnalyser extends App with StrictLogging {

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

  def expectedShotVsActual(intervalSeconds: Int, lines: Seq[LogLine]) = {
    val photosTaken = lines.filter(_.message.contains("Taking photo"))
    val baseLine = photosTaken.head.time
    photosTaken.zipWithIndex.map {
      case (photo, index) => {
        val expectedTime = baseLine.plusSeconds(index * intervalSeconds)
        val diff = MILLIS.between(expectedTime, photo.time)
        logger.info(s"diff: $diff millis \t exp: $expectedTime \t act: ${photo.time}")
      }
    }
  }

  val log =
    """
22:27:12.813:::I:::             Clock: SystemClock[Europe/London]
22:27:12.827:::I:::      Dummy Camera: false
22:27:12.827:::I:::  Images Base Path: /home/pi/dev/ettl/captured-images
22:27:12.828:::I:::     # of Captures: 10
22:27:12.828:::I:::      Set Settings: false
22:27:12.829:::I:::          Interval: 4 seconds
22:27:12.830:::I:::Raw File Extension: CR2
22:27:13.130:::I:::Connecting to camera...
22:27:15.667:::I:::[1/10] Taking photo...
22:27:19.344:::D:::[1/10] Capture took: 3672ms | 3672952638ns
22:27:19.474:::I:::[1/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5477.CR2
22:27:19.476:::D:::[1/10] Schedule took: 3830ms | 3830288810ns
22:27:19.518:::I:::[2/10] Taking photo...
22:27:23.420:::D:::[2/10] Capture took: 3900ms | 3900996392ns
22:27:23.528:::I:::[2/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5478.CR2
22:27:23.529:::D:::[2/10] Schedule took: 4036ms | 4036054248ns
22:27:23.530:::I:::[3/10] Taking photo...
22:27:27.331:::D:::[3/10] Capture took: 3799ms | 3799989444ns
22:27:27.540:::I:::[3/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5479.CR2
22:27:27.542:::D:::[3/10] Schedule took: 4011ms | 4011942348ns
22:27:27.543:::I:::[4/10] Taking photo...
22:27:31.234:::D:::[4/10] Capture took: 3689ms | 3689832025ns
22:27:31.467:::I:::[4/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5480.CR2
22:27:31.468:::D:::[4/10] Schedule took: 3925ms | 3925172510ns
22:27:31.469:::I:::[5/10] Taking photo...
22:27:35.031:::D:::[5/10] Capture took: 3560ms | 3560126120ns
22:27:35.241:::I:::[5/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5481.CR2
22:27:35.242:::D:::[5/10] Schedule took: 3773ms | 3773008237ns
22:27:35.243:::I:::[6/10] Taking photo...
22:27:38.879:::D:::[6/10] Capture took: 3634ms | 3634971856ns
22:27:39.009:::I:::[6/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5482.CR2
22:27:39.010:::D:::[6/10] Schedule took: 3766ms | 3766705928ns
22:27:39.012:::I:::[7/10] Taking photo...
22:27:43.041:::D:::[7/10] Capture took: 4028ms | 4028586476ns
22:27:43.151:::I:::[7/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5483.CR2
22:27:43.152:::D:::[7/10] Schedule took: 4140ms | 4140826592ns
22:27:43.154:::I:::[8/10] Taking photo...
22:27:47.052:::D:::[8/10] Capture took: 3897ms | 3897619848ns
22:27:47.175:::I:::[8/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5484.CR2
22:27:47.176:::D:::[8/10] Schedule took: 4022ms | 4022855727ns
22:27:47.178:::I:::[9/10] Taking photo...
22:27:57.855:::D:::[9/10] Capture took: 10675ms | 10675477812ns
22:27:58.232:::I:::[9/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5485.CR2
22:27:58.235:::D:::[9/10] Schedule took: 11057ms | 11057271454ns
22:27:58.248:::I:::[10/10] Taking photo...
22:28:07.613:::D:::[10/10] Capture took: 9318ms | 9318066270ns
22:28:07.922:::I:::[10/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_22_27_12/_MG_5486.CR2
22:28:07.977:::D:::[10/10] Schedule took: 9729ms | 9729068817ns
22:28:18.407:::I:::App finished
      """

  private val logLines = stringToLogLines(log)
  timesBetweenShots(logLines)
  expectedShotVsActual(4, logLines)
}
