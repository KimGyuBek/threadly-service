package com.threadly.commons.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LogFormatUtils 테스트
 */
class LogFormatUtilsTest {

  @Nested
  @DisplayName("getCaller 테스트")
  class GetCallerTest {

    /*[Case #1] getCaller 호출 시 호출자 정보가 반환되어야 한다*/
    @DisplayName("getCaller - 호출자 정보가 정상적으로 반환되어야 한다")
    @Test
    public void getCaller_shouldReturnCallerInfo_whenCalled() throws Exception {
      //given
      //when
      String caller = LogFormatUtils.getCaller();

      //then
      assertThat(caller).isNotNull();
      assertThat(caller).isNotEmpty();
      assertThat(caller).contains("()");
    }

    /*[Case #2] UnknownCaller가 아닌 값이 반환되어야 한다*/
    @DisplayName("getCaller - UnknownCaller가 아닌 값이 반환되어야 한다")
    @Test
    public void getCaller_shouldNotReturnUnknownCaller_whenCalledNormally() throws Exception {
      //given
      //when
      String caller = LogFormatUtils.getCaller();

      //then
      assertThat(caller).isNotEqualTo("UnknownCaller");
    }
  }

  @Nested
  @DisplayName("logSuccess 테스트")
  class LogSuccessTest {

    /*[Case #1] logSuccess 호출 시 예외가 발생하지 않아야 한다*/
    @DisplayName("logSuccess - 메시지와 함께 호출 시 예외가 발생하지 않아야 한다")
    @Test
    public void logSuccess_shouldNotThrowException_whenCalledWithMessage() throws Exception {
      //given
      String message = "테스트 성공 메시지";

      //when & then
      LogFormatUtils.logSuccess(message);
      // 예외가 발생하지 않으면 테스트 성공
    }
  }

  @Nested
  @DisplayName("logFailure 테스트")
  class LogFailureTest {

    /*[Case #1] logFailure 호출 시 예외가 발생하지 않아야 한다*/
    @DisplayName("logFailure - 메시지와 함께 호출 시 예외가 발생하지 않아야 한다")
    @Test
    public void logFailure_shouldNotThrowException_whenCalledWithMessage() throws Exception {
      //given
      String message = "테스트 실패 메시지";

      //when & then
      LogFormatUtils.logFailure(message);
      // 예외가 발생하지 않으면 테스트 성공
    }
  }
}
