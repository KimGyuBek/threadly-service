package com.threadly.utils;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.token.TokenException;

/**
 * Jwt Token 관련 Utils
 */
public class JwtTokenUtils {

  private static final String BEARER_PREFIX = "Bearer ";

  private JwtTokenUtils() {
    throw new UnsupportedOperationException("JwtTokenUtils는 인스턴스화 할 수 없습니다.");
  }

  /**
   * AuthorizationHeader에서 accessToken 추출
   *
   * @param bearerToken 헤더 값
   * @return 순수 accessToken
   * @throws TokenException
   */
  public static String extractAccessToken(String bearerToken) {
    if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }
    return bearerToken.substring(7);
  }
}
