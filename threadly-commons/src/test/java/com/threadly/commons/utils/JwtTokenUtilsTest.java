package com.threadly.commons.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.token.TokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JwtTokenUtils 테스트
 */
class JwtTokenUtilsTest {

  @Nested
  @DisplayName("extractAccessToken 테스트")
  class ExtractAccessTokenTest {

    /*[Case #1] Bearer 토큰에서 accessToken을 정상적으로 추출해야 한다*/
    @DisplayName("extractAccessToken - Bearer 토큰에서 accessToken이 정상적으로 추출되어야 한다")
    @Test
    public void extractAccessToken_shouldExtractToken_whenBearerTokenIsValid() throws Exception {
      //given
      String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
      String bearerToken = "Bearer " + token;

      //when
      String extractedToken = JwtTokenUtils.extractAccessToken(bearerToken);

      //then
      assertThat(extractedToken).isEqualTo(token);
    }

    /*[Case #2] Bearer 접두사가 없는 경우 예외가 발생해야 한다*/
    @DisplayName("extractAccessToken - Bearer 접두사가 없는 경우 예외가 발생해야 한다")
    @Test
    public void extractAccessToken_shouldThrowException_whenBearerPrefixIsMissing()
        throws Exception {
      //given
      String bearerToken = "InvalidToken";

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> JwtTokenUtils.extractAccessToken(bearerToken));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
    }

    /*[Case #3] null인 경우 예외가 발생해야 한다*/
    @DisplayName("extractAccessToken - null인 경우 예외가 발생해야 한다")
    @Test
    public void extractAccessToken_shouldThrowException_whenBearerTokenIsNull() throws Exception {
      //given
      String bearerToken = null;

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> JwtTokenUtils.extractAccessToken(bearerToken));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
    }

    /*[Case #4] 빈 문자열인 경우 예외가 발생해야 한다*/
    @DisplayName("extractAccessToken - 빈 문자열인 경우 예외가 발생해야 한다")
    @Test
    public void extractAccessToken_shouldThrowException_whenBearerTokenIsEmpty() throws Exception {
      //given
      String bearerToken = "";

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> JwtTokenUtils.extractAccessToken(bearerToken));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
    }
  }

  @Nested
  @DisplayName("인스턴스화 불가 테스트")
  class InstantiationTest {

    /*[Case #1] Utils 클래스는 인스턴스화할 수 없어야 한다*/
    @DisplayName("JwtTokenUtils는 인스턴스화할 수 없어야 한다")
    @Test
    public void constructor_shouldThrowException_whenInstantiated() throws Exception {
      //given
      //when & then
      java.lang.reflect.InvocationTargetException exception = assertThrows(
          java.lang.reflect.InvocationTargetException.class,
          () -> {
            java.lang.reflect.Constructor<JwtTokenUtils> constructor =
                JwtTokenUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
          });

      assertThat(exception.getCause()).isInstanceOf(UnsupportedOperationException.class);
    }
  }
}
