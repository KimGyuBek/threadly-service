package com.threadly.token;

import com.threadly.token.response.TokenPortResponse;
import java.util.Optional;

public interface InsertTokenPort {

  /**
   * 새로운 Token 생성
   * @param createToken
   * @return
   */
  TokenPortResponse create(CreateToken createToken);

}
