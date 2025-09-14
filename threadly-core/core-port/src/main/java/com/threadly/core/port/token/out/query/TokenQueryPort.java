package com.threadly.core.port.token.out.query;

/**
 * token 조회 port
 */
public interface TokenQueryPort {

  /**
   * userId로 refreshToken 조회
   * @param userId
   * @return
   */
  boolean existsRefreshTokenByUserId(String userId);

  /**
   * userId로 refreshToken 조회
   * @param userId
   * @return
   */
  String findRefreshTokenByUserId(String userId);

  /**
   * accessToken으로 blacklist 토큰이 존재하는지 조회
   * @param accessToken
   * @return
   */
  boolean existsBlackListTokenByAccessToken(String accessToken);

}
