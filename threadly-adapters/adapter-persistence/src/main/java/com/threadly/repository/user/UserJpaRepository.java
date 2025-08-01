package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
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
   *
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
   *
   * @param userId
   * @param statusType
   */
  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.userStatusType = :statusType "
          + "where u.userId = :userId")
  void updateStatus(@Param("userId") String userId,
      @Param("statusType") UserStatusType statusType);


  /**
   * 주어진 userId에 해당하는 사용자의 phone 업데이트
   *
   * @param userId
   * @param phone
   */
  @Modifying
  @Query("""
      update UserEntity  u
      set u.phone = :phone
      where u.userId = :userId
      """)
  void updatePhoneByUserId(@Param("userId") String userId, @Param("phone") String phone);

  /**
   * 주어진 userId에 해당하는 사용자의 password 변경
   *
   * @param userId
   * @param newPassword
   */
  @Modifying
  @Query("""
      update UserEntity u
      set u.password = :newPassword
      where u.userId = :userId
      """)
  void updatePasswordByUserId(@Param("userId") String userId,
      @Param("newPassword") String newPassword);

  /**
   * 주어진 userId에 해당하는 사용자의 is_private 변경
   *
   * @param userId
   * @param isPrivate
   */
  @Modifying
  @Query("""
      update UserEntity u
       set u.isPrivate = :isPrivate 
       where u.userId = :userId
      """)
  void updatePrivacyByUserId(@Param("userId") String userId, @Param("isPrivate") boolean isPrivate);
}
