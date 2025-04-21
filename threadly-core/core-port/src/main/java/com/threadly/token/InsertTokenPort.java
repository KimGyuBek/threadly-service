package com.threadly.token;

import com.threadly.token.response.TokenPortResponse;

public interface InsertTokenPort {

  /**
   * refreshToken 저장
   * @param insertRefreshToken
   * @return
   */
  void save(InsertRefreshToken insertRefreshToken);

}
