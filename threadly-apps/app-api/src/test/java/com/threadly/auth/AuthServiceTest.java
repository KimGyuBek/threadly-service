package com.threadly.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.threadly.auth.token.response.TokenReissueResponse;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertRefreshToken;
import com.threadly.token.InsertTokenPort;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * authService 테스트
 */
@ActiveProfiles("test")
@SpringBootTest
class AuthServiceTest {


  @Autowired
  private AuthService authService;

  @Autowired
  private FetchTokenPort fetchTokenPort;

  @Autowired
  InsertTokenPort insertTokenPort;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  /*reissueLogin 테스트*/
  /*[Case #1] 저장된 refreshToken이 없고 새로 발급하는 겨우*/
  @DisplayName("reissueLoginTest - 새로 발급하는 경우")
  @Test
  public void reissueLogin_shouldReturnLoginTokens_whenRefreshTokenNotExist() throws Exception {

    //given
    String userId = "user1";
    Duration duration = Duration.ofSeconds(5);
    String refreshToken = jwtTokenProvider.generateToken(userId, duration);

    //when
    TokenReissueResponse result = authService.reissueLoginToken(refreshToken);

    //then
    boolean isTokenExists = fetchTokenPort.existsRefreshTokenByUserId(userId);
    String refreshTokenByUserId = fetchTokenPort.findRefreshTokenByUserId(userId);

    assertAll(
        () -> assertNotNull(result.getRefreshToken()),
        () -> assertNotNull(result.getAccessToken()),
        () -> assertTrue(isTokenExists),
        () -> assertThat(result.getRefreshToken()).isEqualTo(
            refreshTokenByUserId)
    );


  }

  /*[Case #2] 이미 저장된 refreshToken이 있어서 덮어쓰는 경우*/
  @Test
  public void reissueToken_shouldReturnLoginTokens_whenRefreshTokenExists() throws Exception {
    //given
    String userId = "user1";
    Duration duration = Duration.ofSeconds(5);

    String refreshToken = jwtTokenProvider.generateToken(userId, duration);

    insertTokenPort.save(InsertRefreshToken.builder()
        .userId(userId)
        .refreshToken(refreshToken)
        .duration(duration)
        .build());

    //when
    TokenReissueResponse result = authService.reissueLoginToken(refreshToken);

    String refreshTokenByUserId = fetchTokenPort.findRefreshTokenByUserId(userId);
    boolean isTokenExists = fetchTokenPort.existsRefreshTokenByUserId(userId);

    //then
    assertAll(
        () -> assertNotNull(result.getAccessToken()),
        () -> assertNotNull(result.getRefreshToken()),
        () -> assertTrue(isTokenExists),
        () -> assertThat(result.getRefreshToken()).isEqualTo(refreshTokenByUserId)
    );
  }


}