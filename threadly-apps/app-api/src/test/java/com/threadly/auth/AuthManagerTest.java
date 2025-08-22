package com.threadly.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.adapter.redis.repository.auth.TestRedisHelper;
import com.threadly.commons.security.JwtTokenProvider;
import com.threadly.core.port.token.FetchTokenPort;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/**
 * authService 테스트
 */
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class AuthManagerTest {


  @Autowired
  private AuthManager authManager;

  @Autowired
  private FetchTokenPort fetchTokenPort;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private TestRedisHelper loginAttemptHelper;

  @Autowired
  private TtlProperties ttlProperties;

  @BeforeEach
  void setUp() {
    loginAttemptHelper.clearRedis();
  }

//  /*reissueLogin 테스트*/
//  /*[Case #1] 저장된 refreshToken이 있고, 클라이언트가 같은 토큰을 전달하면 새로운 토큰을 발급한다.*/
//  @DisplayName("저장된 refreshToken이 있고, 클라이언트가 같은 토큰을 전달하면 새로운 토큰을 발급한다")
//  @Test
//  public void reissueLogin_shouldReturnNewToken_whenRefreshMatchesStoredToken() throws Exception {
//
//    //given
//    String userId = "user1";
//    String refreshToken = jwtTokenProvider.createRefreshToken(userId);
//    String token = "Bearer " + refreshToken;
//    loginAttemptHelper.insert("token:refresh:" + userId, refreshToken,
//        ttlProperties.getRefreshToken());
//
//    //when
//    TokenReissueResponse result = authManager.reissueLoginToken(token);
//
//    //then
//    String refreshTokenByUserId = fetchTokenPort.findRefreshTokenByUserId(userId);
//
//    assertAll(
//        () -> assertNotNull(result.getAccessToken()),
//        () -> assertNotNull(result.getRefreshToken()),
//        () -> assertThat(result.getRefreshToken()).isEqualTo(refreshTokenByUserId)
//    );
//  }

//  /*[Case #2] 저장된 refreshToken이 없는 경우 예외를 던진다.*/
//  @DisplayName("저장된 refreshToken이 없는 경우 예외를 던진다.")
//  @Test
//  public void reissueToken_shouldReturnException_whenRefreshTokenNotExists() throws Exception {
//    //given
//    String userId = "user1";
//    String refreshToken =
//        "Bearer " + jwtTokenProvider.createAccessToken(userId, UserType.USER.name(), true);
//
//    //when
//    assertThrows(TokenException.class, () -> authManager.reissueLoginToken(refreshToken));
//  }


}