package com.threadly.token;

import com.threadly.token.response.TokenResponse;
import com.threadly.user.response.UserResponse;

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
