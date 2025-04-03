package com.threadly.entity.token;

import com.threadly.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Table(name = "tokens")
@Getter
public class TokenEntity extends BaseEntity {

  @Id
  @Column(name = "token_id")
  private String tokenId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "access_token")
  private String accessToken;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "access_token_expires_at")
  private LocalDateTime accessTokenExpiresAt;

  @Column(name = "refresh_token_expires_at")
  private LocalDateTime refreshTokenExpiresAt;


}
