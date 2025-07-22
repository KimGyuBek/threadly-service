package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.entity.user.UserProfileEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByEmail(String email);


  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.isEmailVerified = :isEmailVerified "
          + "where u.userId = :userId")
  void updateEmailVerification(@Param("userId") String userId,
      @Param("isEmailVerified") boolean isEmailVerified);

  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.userProfile = :userProfileEntity "
          + "where u.userId = :userId"
  )
  void setUserProfile(@Param("userId") String userId,
      @Param("userProfileEntity") UserProfileEntity userProfileEntity);

  @Query(value =
      "select u from UserEntity u "
          + "left join "
          + "fetch u.userProfile "
          + "where u.userId = :userId")
  Optional<UserEntity> findByUserIdWithUserProfile(@Param("userId") String userId);

  @Query(value =
      "select up "
          + "from UserEntity u "
          + "left join u.userProfile up "
          + "where u.userId = :userId"
  )
  Optional<UserProfileEntity> findUserProfileByUserId(@Param("userId") String userId);

  /**
   * userId로 userProfile 존재 검증
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select exists(select 1
                    from users
                    where user_id = :userId and user_profile_id is not null)
      """, nativeQuery = true)
  boolean existsByUserIdAndUserProfileIsNotNull(@Param("userId") String userId);
}
