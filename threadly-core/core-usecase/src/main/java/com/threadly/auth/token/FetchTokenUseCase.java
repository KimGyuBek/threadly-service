package com.threadly.auth.token;

public interface FetchTokenUseCase {

  /**
   * token upsert
   * @param userId
   * @return
   */

  /**
   * accessToken으로 userId 조회
   * @param accessToken
   * @return
   */
  String  findUserIdByAccessToken(String accessToken);

}
