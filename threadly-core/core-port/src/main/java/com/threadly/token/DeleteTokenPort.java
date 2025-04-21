package com.threadly.token;

/**
 * redis token 삭제 port
 */
public interface DeleteTokenPort {

  /**
   * refreshToken 삭제
   * @param refreshToken
   */
  void deleteRefreshToken(String refreshToken);


}
