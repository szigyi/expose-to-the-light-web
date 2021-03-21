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
      16:16:08.566:::I:::             Clock: SystemClock[Europe/London]
16:16:08.588:::I:::      Dummy Camera: false
16:16:08.589:::I:::  Images Base Path: /home/pi/dev/ettl/captured-images
16:16:08.590:::I:::     # of Captures: 10
16:16:08.591:::I:::      Set Settings: false
16:16:08.592:::I:::          Interval: 11 seconds
16:16:08.592:::I:::Raw File Extension: CR2
16:16:08.826:::I:::Connecting to camera...
16:16:10.771:::I:::[1/10] Taking photo...
16:16:21.663:::D:::[1/10] Capture took: 10887ms | 10887937655ns
16:16:21.757:::I:::[1/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5456.CR2
16:16:21.758:::D:::[1/10] Schedule took: 11008ms | 11008248538ns
16:16:21.792:::I:::[2/10] Taking photo...
16:16:32.929:::D:::[2/10] Capture took: 11135ms | 11135218906ns
16:16:33.001:::I:::[2/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5457.CR2
16:16:33.002:::D:::[2/10] Schedule took: 11227ms | 11227661369ns
16:16:33.003:::I:::[3/10] Taking photo...
16:16:43.988:::D:::[3/10] Capture took: 10984ms | 10984509753ns
16:16:44.062:::I:::[3/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5458.CR2
16:16:44.063:::D:::[3/10] Schedule took: 11059ms | 11059740311ns
16:16:44.064:::I:::[4/10] Taking photo...
16:16:55.179:::D:::[4/10] Capture took: 11114ms | 11114950575ns
16:16:55.254:::I:::[4/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5459.CR2
16:16:55.256:::D:::[4/10] Schedule took: 11192ms | 11192175708ns
16:16:55.257:::I:::[5/10] Taking photo...
16:17:06.429:::D:::[5/10] Capture took: 11171ms | 11171827446ns
16:17:06.514:::I:::[5/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5460.CR2
16:17:06.515:::D:::[5/10] Schedule took: 11258ms | 11258803630ns
16:17:06.516:::I:::[6/10] Taking photo...
16:17:17.209:::D:::[6/10] Capture took: 10691ms | 10691549157ns
16:17:17.296:::I:::[6/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5461.CR2
16:17:17.297:::D:::[6/10] Schedule took: 10780ms | 10780598561ns
16:17:17.298:::I:::[7/10] Taking photo...
16:17:27.938:::D:::[7/10] Capture took: 10639ms | 10639794056ns
16:17:28.009:::I:::[7/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5462.CR2
16:17:28.010:::D:::[7/10] Schedule took: 10712ms | 10712631811ns
16:17:28.011:::I:::[8/10] Taking photo...
16:17:38.828:::D:::[8/10] Capture took: 10815ms | 10815976138ns
16:17:38.899:::I:::[8/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5463.CR2
16:17:38.900:::D:::[8/10] Schedule took: 10889ms | 10889200038ns
16:17:38.901:::I:::[9/10] Taking photo...
16:17:50.019:::D:::[9/10] Capture took: 11116ms | 11116316377ns
16:17:50.089:::I:::[9/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5464.CR2
16:17:50.090:::D:::[9/10] Schedule took: 11189ms | 11189073507ns
16:17:50.095:::I:::[10/10] Taking photo...
16:18:01.209:::D:::[10/10] Capture took: 11112ms | 11112457524ns
16:18:01.300:::I:::[10/10] Saved image: /home/pi/dev/ettl/captured-images/2021_03_21_16_16_08/_MG_5465.CR2
16:18:01.301:::D:::[10/10] Schedule took: 11205ms | 11205470400ns
16:18:01.443:::I:::App finished
      """

  private val logLines = stringToLogLines(log)
  timesBetweenShots(logLines)
  expectedShotVsActual(11, logLines)
}
