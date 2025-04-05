package com.threadly.token;

import java.util.Optional;

public interface FetchTokenPort {

  /**
   * userId로 token 조회
   * @param userId
   * @return
   */
  Optional<Token> findByUserId(String userId);

  /**
   * accessToken으로 userId 조회
   * @param accessToken
   * @return
   */
  Optional<String> findUserIdByAccessToken(String accessToken);


}
