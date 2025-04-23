package com.threadly.token;

public interface FetchTokenPort {


//  /**
//   * refreshToken으로 userId 조회
//   * @param refreshToken
//   * @return
//   */
//  String findUserIdByRefreshToken(String refreshToken);

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

}
