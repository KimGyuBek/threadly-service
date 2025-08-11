package com.threadly.commons.utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Time Utils
 */
public class TimeUtils {

  /**
   * 주어진 startTime, endTime으로 실행시간 계산 후 포맷팅
   *
   * @param startTime
   * @param endTime
   * @return
   */
  public static String getExecutionTimeFormatted(LocalDateTime startTime, LocalDateTime endTime) {
    long millis = Duration.between(startTime, endTime).toMillis();

    long hours = (millis / 1000) / 3600;
    long minutes = (millis / 1000 % 3600) / 60;
    long seconds = (millis / 1000) % 60;
    long remainMillis = millis % 1000;

    return String.format("%02dh %02dm %02ds %03dms", hours, minutes, seconds, remainMillis);
  }

}
