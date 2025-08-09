package com.threadly.core.port.token;

/**
 * token 삭제 port
 */
public interface DeleteTokenPort {

  /**
   * refreshToken 삭제
   * @param userId
   */
  void deleteRefreshToken(String userId);

}
