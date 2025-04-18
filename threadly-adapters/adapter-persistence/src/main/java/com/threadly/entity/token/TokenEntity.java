package com.threadly.entity.token;

import com.threadly.entity.BaseEntity;
import com.threadly.token.response.TokenPortResponse;
import com.threadly.user.UserType;
import com.threadly.util.RandomUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tokens")
@Getter
@RequiredArgsConstructor
@AllArgsConstructor()
@ToString
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

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  private UserType userType;

  @Column(name = "access_token_expires_at")
  private LocalDateTime accessTokenExpiresAt;

  @Column(name = "refresh_token_expires_at")
  private LocalDateTime refreshTokenExpiresAt;

  /**
   * TokenEntity -> TokenPortResponse
   *
   * @return
   */
  public TokenPortResponse toTokenPortResponse() {
    return TokenPortResponse.builder()
        .tokenId(tokenId)
        .userId(userId)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userType(userType)
        .accessTokenExpiresAt(accessTokenExpiresAt)
        .refreshTokenExpiresAt(refreshTokenExpiresAt)
        .build();
  }


  /**
   * newTokenEntity
   *
   * @param userId
   * @param accessToken
   * @param refreshToken
   * @param userType
   * @param accessTokenExpiresAt
   * @param refreshTokenExpiresAt
   * @return
   */
  public static TokenEntity newTokenEntity(String userId, String accessToken, String refreshToken,
      UserType userType, LocalDateTime accessTokenExpiresAt, LocalDateTime refreshTokenExpiresAt
  ) {
    return new TokenEntity(
        RandomUtils.generateNanoId(),
        userId,
        accessToken,
        refreshToken,
        userType,
        accessTokenExpiresAt,
        refreshTokenExpiresAt
    );
  }

  public void updateToken(String accessToken, String refreshToken,
      LocalDateTime accessTokenExpiresAt, LocalDateTime refreshTokenExpiresAt) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpiresAt = accessTokenExpiresAt;
    this.refreshTokenExpiresAt = refreshTokenExpiresAt;
  }
}
