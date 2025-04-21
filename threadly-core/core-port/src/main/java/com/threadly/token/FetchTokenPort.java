package com.threadly.token;

import java.util.Optional;

public interface FetchTokenPort {


  /**
   * refreshToken으로 userId 조회
   * @param refreshToken
   * @return
   */
  String findUserIdByRefreshToken(String refreshToken);

}
