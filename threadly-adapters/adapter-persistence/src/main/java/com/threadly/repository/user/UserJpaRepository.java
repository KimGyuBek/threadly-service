package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByEmail(String email);

}
