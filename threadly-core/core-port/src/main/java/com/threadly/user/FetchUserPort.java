package com.threadly.user;

import java.util.Optional;

public interface FetchUserPort {

  /**
   * Email로 User 조회
   *
   * @param email
   * @return
   */
  Optional<User> findByEmail(String email);

  /**
   * userId로 User 조회
   *
   * @param userId
   * @return
   */
  Optional<User> findByUserId(String userId);

}
