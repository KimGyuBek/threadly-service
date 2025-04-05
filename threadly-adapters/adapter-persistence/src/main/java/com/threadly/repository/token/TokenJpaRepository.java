package com.threadly.repository.token;

import com.threadly.entity.token.TokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, String> {

  Optional<TokenEntity> findByUserId(String userId);

  @Query("select t.userId from TokenEntity t where t.accessToken = :accessToken")
  Optional<String> findUserIdByAccessToken(@Param("accessToken") String accessToken);



}
