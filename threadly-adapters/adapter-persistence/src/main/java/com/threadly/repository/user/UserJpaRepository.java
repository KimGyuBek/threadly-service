package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
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


}
