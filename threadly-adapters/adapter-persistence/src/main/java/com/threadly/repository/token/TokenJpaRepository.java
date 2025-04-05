package com.threadly.repository.token;

import com.threadly.entity.token.TokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, String> {

  Optional<TokenEntity> findByUserId(String userId);



}
