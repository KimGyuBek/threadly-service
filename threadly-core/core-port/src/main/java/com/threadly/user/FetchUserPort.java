package com.threadly.user;

import java.util.Optional;

public interface FetchUserPort {

  /**
   * Email로 User 조회
   *
   * @param email
   * @return
   */
  Optional<UserPortResponse> findByEmail(String email);

  /**
   * userId로 User 조회
   * @param userId
   * @return
   */
  Optional<UserPortResponse> findByUserId(String userId);


}
