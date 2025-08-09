package com.threadly.core.port.token;

/**
 * redis token 삭제 port
 */
public interface UpsertTokenPort {

  /**
   * refreshToken upsert
   * @param upsertRefreshToken
   */
  void upsertRefreshToken(UpsertRefreshToken upsertRefreshToken);


}
