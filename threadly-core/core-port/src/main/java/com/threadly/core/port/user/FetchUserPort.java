package com.threadly.core.port.user;

import com.threadly.core.domain.user.User;
import com.threadly.core.domain.user.UserStatusType;
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

  /**
   * 주어진 userId에 해당하는 사용자의 isPrivate 조회
   * @param userId
   * @return
   */
  boolean isUserPrivate(String userId);

  /**
   * 주어진 userId에 해당하는 사용자의 status 조회
   * @param userId
   * @return
   */
  Optional<UserStatusType> getUserStatus(String userId);

}
