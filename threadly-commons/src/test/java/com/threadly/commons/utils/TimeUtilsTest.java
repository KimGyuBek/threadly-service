package com.threadly.commons.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TimeUtils 테스트
 */
class TimeUtilsTest {

  @Nested
  @DisplayName("getExecutionTimeFormatted 테스트")
  class GetExecutionTimeFormattedTest {

    /*[Case #1] 밀리초만 있는 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 밀리초만 있는 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenOnlyMilliseconds()
        throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 500_000_000); // 500ms

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("00h 00m 00s 500ms");
    }

    /*[Case #2] 초만 있는 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 초만 있는 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenOnlySeconds() throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 45);

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("00h 00m 45s 000ms");
    }

    /*[Case #3] 분만 있는 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 분만 있는 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenOnlyMinutes() throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 0, 30, 0);

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("00h 30m 00s 000ms");
    }

    /*[Case #4] 시간만 있는 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 시간만 있는 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenOnlyHours() throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 2, 0, 0);

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("02h 00m 00s 000ms");
    }

    /*[Case #5] 모든 단위가 포함된 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 모든 단위가 포함된 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenAllUnitsIncluded()
        throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 3, 45, 23, 567_000_000); // 3h 45m 23s 567ms

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("03h 45m 23s 567ms");
    }

    /*[Case #6] 0초인 경우 올바르게 포맷팅되어야 한다*/
    @DisplayName("getExecutionTimeFormatted - 0초인 경우 올바르게 포맷팅되어야 한다")
    @Test
    public void getExecutionTimeFormatted_shouldFormatCorrectly_whenZeroTime() throws Exception {
      //given
      LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

      //when
      String result = TimeUtils.getExecutionTimeFormatted(startTime, endTime);

      //then
      assertThat(result).isEqualTo("00h 00m 00s 000ms");
    }
  }
}
