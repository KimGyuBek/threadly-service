package com.threadly.token;

import com.threadly.token.response.TokenPortResponse;
import java.util.Optional;

public interface FetchTokenPort {

  /**
   * userId로 token 조회
   * @param userId
   * @return
   */
  Optional<TokenPortResponse> findByUserId(String userId);



}
