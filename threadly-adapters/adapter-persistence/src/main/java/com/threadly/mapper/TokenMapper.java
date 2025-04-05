package com.threadly.mapper;

import com.threadly.entity.token.TokenEntity;
import com.threadly.token.Token;
import org.springframework.stereotype.Component;

public class TokenMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static Token toDomain(TokenEntity entity) {
    return Token.builder()
        .userId(entity.getUserId())
        .accessToken(entity.getAccessToken())
        .refreshToken(entity.getRefreshToken())
        .userType(entity.getUserType())
        .accessTokenExpiresAt(entity.getAccessTokenExpiresAt())
        .refreshTokenExpiresAt(entity.getRefreshTokenExpiresAt())
        .build();
  }

  /**
   * domain -> entity
   * @param domain
   * @return
   */
  public static TokenEntity toEntity(Token domain) {
    return new TokenEntity(
        domain.getTokenId(),
        domain.getUserId(),
        domain.getAccessToken(),
        domain.getRefreshToken(),
        domain.getUserType(),
        domain.getAccessTokenExpiresAt(),
        domain.getRefreshTokenExpiresAt()
    );
  }

}
