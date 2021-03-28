package hu.szigyi.ettl.web.util

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId, ZonedDateTime}

object LocalTimeUtil {

  /**
   * Log's timestamp is in the local's zone. But we have to translate it into UTC as this data will appear
   * on the UI eventually, and UI does not handle anything else in this matter only UTC
   * @param t the actual local time
   * @param originalZone the zone where the local time is made in
   * @param newZone the zone where you want to translate the local time
   * @return
   */
  def translateTimeToAnotherZone(t: LocalTime, originalZone: ZoneId, newZone: ZoneId): LocalTime = {
    val systemDefaultTime: ZonedDateTime = LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()), t).atZone(originalZone)
    val utcTime                          = systemDefaultTime.withZoneSameInstant(newZone)
    utcTime.toLocalTime
  }
}
