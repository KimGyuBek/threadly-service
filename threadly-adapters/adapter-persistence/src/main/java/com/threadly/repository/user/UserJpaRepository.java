package com.threadly.repository.user;

import com.threadly.entity.user.UserEntity;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUserId(String userId);


}
