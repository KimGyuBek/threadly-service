package com.threadly.token;

import com.threadly.user.UserType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Token 도메인
 */
@Getter
@Builder
public class Token {

  private String tokenId;
  private String userId;
  private String accessToken;
  private String refreshToken;
  private UserType userType;
  private LocalDateTime accessTokenExpiresAt;
  private LocalDateTime refreshTokenExpiresAt;


  /**
   * 새로운 토큰 생성
   *
   * @param userId
   * @param accessToken
   * @param refreshToken
   * @return
   */
  public static Token upsertToken(String userId, String accessToken, String refreshToken) {
    LocalDateTime now = LocalDateTime.now();

    return
        Token.builder()
            .tokenId(null)
            .userId(userId)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userType(UserType.USER)
            .accessTokenExpiresAt(getAccessTokenExpiresAt(now))
            .refreshTokenExpiresAt(getRefreshTokenExpiresAt(now))
            .build();

  }

  /**
   * 토큰 업데이트
   *
   * @param accessToken
   * @param updateToken
   */
  public void updateToken(String accessToken, String updateToken) {

    /*토큰 값 업데이트*/
    this.accessToken = accessToken;
    this.refreshToken = updateToken;

    /*만료시간 업데이트*/
    LocalDateTime now = LocalDateTime.now();

    this.accessTokenExpiresAt = getAccessTokenExpiresAt(now);
    this.refreshTokenExpiresAt = getRefreshTokenExpiresAt(now);
  }

  /*TODO token 만료시간 properties로 만들어서 하나로 관리하기*/
  /*token 만료시간 설정*/
  private static LocalDateTime getAccessTokenExpiresAt(LocalDateTime now) {
    return now.plusMinutes(15);
  }

  private static LocalDateTime getRefreshTokenExpiresAt(LocalDateTime now) {
    return now.plusHours(6);
  }

}
