package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.entity.user.UserProfileEntity;
import com.threadly.user.UserStatusType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByEmail(String email);

  /**
   * isEmailVerified 변경
   * @param userId
   * @param isEmailVerified
   */
  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.isEmailVerified = :isEmailVerified "
          + "where u.userId = :userId")
  void updateEmailVerification(@Param("userId") String userId,
      @Param("isEmailVerified") boolean isEmailVerified);

  /**
   * status 변경
   * @param userId
   * @param statusType
   */
  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.userStatusType = :statusType "
          + "where u.userId = :userId")
  void updateStatus(@Param("userId") String userId,
      @Param("status") UserStatusType statusType);
}
