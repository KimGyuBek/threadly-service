package com.threadly.token;

/**
 * redis token 삭제 port
 */
public interface UpsertToken {

  /**
   * refreshToken upsert
   * @param upsertRefreshToken
   */
  void upsertRefreshToken(UpsertRefreshToken upsertRefreshToken);


}
