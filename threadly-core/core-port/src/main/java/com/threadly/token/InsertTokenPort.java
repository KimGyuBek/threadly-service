package com.threadly.token;

public interface InsertTokenPort {

  /**
   * refreshToken 저장
   *
   * @param insertRefreshToken
   * @return
   */
  void save(InsertRefreshToken insertRefreshToken);

}
