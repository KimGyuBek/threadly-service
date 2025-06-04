package com.threadly.scenario.auth;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.auth.token.response.TokenReissueResponse;
import com.threadly.auth.request.UserLoginRequest;
import com.threadly.repository.auth.TestRedisHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * auth - login 시나리오 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class LoginScenarioTest extends BaseApiTest {

  private static final int MAX_LOGIN_ATTEMPTS = 5;

  @Autowired
  private TestRedisHelper loginAttemptHelper;

  @BeforeEach
  public void setUp() throws Exception {
    loginAttemptHelper.clearRedis();
    super.setUpDefaultUser();
  }

  /* 로그인 시나리오 테스트*/
  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("로그인 시나리오 테스트")
  class loginTest {

    /*
     * [Case #1]
     * 로그인 성공 -> "/" 접속 -> 요청 성공 (정상 동작)
     * */
    @Order(1)
    @DisplayName("성공-1. 로그인 후 '/' 접속")
    @Test
    @Transactional
    public void accessProtectedResource_shouldSucceed_whenLoggedIn() throws Exception {
      //given

      /*로그인 요청 body*/
      String loginRequestBody = generateRequestBody(
          UserLoginRequest.builder()
              .email(EMAIL_VERIFIED_USER_1)
              .password(PASSWORD)
              .build()

      );

      //when
      /*1. 로그인 요청 전송*/
      CommonResponse<LoginTokenResponse> loginResponse = sendPostRequest(loginRequestBody,
          "/api/auth/login",
          status().isOk(), new TypeReference<CommonResponse<LoginTokenResponse>>() {
          }, Map.of());
      String accessToken = loginResponse.getData().accessToken();

      /*2. "/" 접속*/
      CommonResponse response = sendGetRequest(accessToken, "/", status().isNotFound()
      );

      //then
      assertAll(
          () -> assertTrue(loginResponse.isSuccess()),
          () -> assertTrue(response.isSuccess())
      );
    }

    /*
     * [Case #2]
     * 로그인 성공 -> accessToken 없이 "/" 접속 -> 401 Unauthorized
     * */
    @Order(2)
    @DisplayName("성공-2. 로그인 후 만료된 accessToken으로  접속'/' -> TLY3000 -> 재접속")
    @Test
    public void accessProtectedResource_sholudReturnUnAuthorized_whenAccessTokenNotExists()
        throws Exception {
      //given
      //when
      /*로그인*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1,
          PASSWORD,
          new TypeReference<>() {
          },
          status().isOk()
      );

      Thread.sleep(3500);

      /* accessToken 만료 후 '/' 접속*/
      CommonResponse tokenExpiredResponse = sendGetRequest(
          loginResponse.getData().accessToken(), "/", status().isUnauthorized());

      //then
      /*로그인 응답 검증*/
      assertAll(
          () -> assertTrue(loginResponse.isSuccess()),
          () -> assertNotNull(loginResponse.getData().accessToken())
      );

      /*토큰 만료 응답 검증*/
      assertAll(
          () -> assertFalse(tokenExpiredResponse.isSuccess()),
          () -> assertThat(tokenExpiredResponse.getCode()).isEqualTo(
              ErrorCode.TOKEN_EXPIRED.getCode())
      );
    }

    /*
     * [Case #3]
     * 로그인 성공 -> accessToken 만료 후 "/" 접속 -> 401 -> accessToken 재발급 후 재 접속 -> 성공
     * */
    @Order(3)
    @DisplayName("성공-3. 로그인 후 만료된 accessToken으로  접속'/' -> TLY3000 -> 재발급 후 재 접속")
    @Test
    public void accessProtectedResource_shouldSucceed_afterAccessTokenExpired_whenReissueAccessToken()
        throws Exception {
      //given
      //when

      /*로그인*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
          }, status().isOk()
      );

      String accessToken = loginResponse.getData().accessToken();
      String refreshToken = loginResponse.getData().refreshToken();

      /*토큰 만료까지 대기*/
      Thread.sleep(3500);

      /*accessToken 만료 후 '/' 접속*/
      CommonResponse tokenExpiredResponse = sendGetRequest(
          accessToken, "/", status().isUnauthorized());

      /*accessToken 재발급*/
      CommonResponse<TokenReissueResponse> tokenReissueResponse = sendPostRequest(
          "", "/api/auth/reissue", status().isOk(),
          new TypeReference<CommonResponse<TokenReissueResponse>>() {
          }, Map.of("X-refresh-token", "Bearer " + refreshToken)
      );
      String reIssueAccessToken = tokenReissueResponse.getData().getAccessToken();


      /*accessToken 재발급 후 '/' 재접속*/
      CommonResponse response = sendGetRequest(
          reIssueAccessToken, "/", status().isNotFound());

      //then
      /*로그인 응답 검증*/
      assertAll(
          () -> assertTrue(loginResponse.isSuccess()),
          () -> assertNotNull(loginResponse.getData().accessToken())
      );

      /*토큰 만료 응답 검증*/
      assertAll(
          () -> assertFalse(tokenExpiredResponse.isSuccess()),
          () -> assertThat(tokenExpiredResponse.getCode()).isEqualTo(
              ErrorCode.TOKEN_EXPIRED.getCode())
      );

      /*토큰 재발급 응답 검증*/
      assertAll(
          () -> assertNotNull(tokenReissueResponse.getData().getRefreshToken()),
          () -> assertNotNull(tokenReissueResponse.getData().getAccessToken()),
          () -> assertTrue(tokenReissueResponse.isSuccess())
      );

      /*재발급 후 재접속 응답 검증*/
      assertAll(() -> assertTrue(response.isSuccess()));
    }

    @Order(4)
    /* [case #4] 로그인시 재발급되는 토큰 비교 검증*/
    @DisplayName("성공-4. 여러 번 로그인 시 매번 새로운 accessToken, refreshToken 발급")
    @Test
    public void verifyLoginTokens_shouldBeUnique_whenForEachLogin() throws Exception {
      //given

      List<String> accessTokenList = new ArrayList<>();
      List<String> refreshTokenList = new ArrayList<>();

      CommonResponse<LoginTokenResponse> loginResponse;

      for (int i = 0; i < 50; i++) {
        loginResponse = sendLoginRequest(
            EMAIL_VERIFIED_USER_1, PASSWORD, new TypeReference<>() {
            }, status().isOk()
        );
        accessTokenList.add(loginResponse.getData().accessToken());
        refreshTokenList.add(loginResponse.getData().refreshToken());
      }

      //when
      HashSet<String> accessTokenSet = new HashSet<>(accessTokenList);
      HashSet<String> refreshTokenSet = new HashSet<>(refreshTokenList);

      //then
      assertAll(
          () -> assertThat(accessTokenSet.size()).isEqualTo(accessTokenList.size()),
          () -> assertThat(refreshTokenSet.size()).isEqualTo(refreshTokenSet.size())
      );
    }

    /*[Case #5] 로그인 제한 후 시간이 지나면 초기화 되어야 한다.*/
    @Order(5)
    @DisplayName("성공-5. 로그인 제한 후 시간이 지나면 초기화, 이후 로그인 시도")
    @Test
    public void checkLoginAttempt_shouldAllowLogin_whenTtlExpires() throws Exception {
      //given
      String invalidPassword = "invalid_password";
      //when

      /*잘봇된 로그인 5회 시도*/
      for (int i = 0; i < MAX_LOGIN_ATTEMPTS; i++) {
        sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword, new TypeReference<>() {
        }, status().isUnauthorized());
      }
      CommonResponse<Object> response1 = sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword,
          new TypeReference<>() {
          }, status().isTooManyRequests());

      /*ttl 만료까지 대기*/
      Thread.sleep(5200);

      /*정상 로그인 시도*/
      CommonResponse<LoginTokenResponse> response2 = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          PASSWORD,
          new TypeReference<>() {
          }, status().isOk());

      //then
      assertAll(
          () -> assertFalse(response1.isSuccess()),
          () -> assertThat(response1.getCode()).isEqualTo(
              ErrorCode.LOGIN_ATTEMPT_EXCEEDED.getCode())
      );

      assertTrue(response2.isSuccess());

    }

    @Order(6)
    /*[Case #6] 잘못된 비밀번호로 로그인 5회이상 시도할경우 - 로그인 제한*/
    @DisplayName("실패-1. 잘못된 비밀번호로 5회 이상 로그인 시도 할 경우")
    @Test
    public void checkLoginAttempt_shouldFail_whenExceededMaxAttempts() throws Exception {
      //given
      String invalidPassword = "invalid_password";
      //when

      /*로그인 5회 시도*/
      for (int i = 0; i < MAX_LOGIN_ATTEMPTS; i++) {
        sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword, new TypeReference<>() {
        }, status().isUnauthorized());
      }
      CommonResponse<Object> response = sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword,
          new TypeReference<>() {
          }, status().isTooManyRequests());

      //then
      assertFalse(response.isSuccess());
      assertThat(response.getCode()).isEqualTo(ErrorCode.LOGIN_ATTEMPT_EXCEEDED.getCode());

    }

    /*[Case #7] 정상 로그인시 시도 횟수가 초기화되고 이후 잘못된 비밀번호로 5회 초과시 로그인 제한이 걸린다*/
    @Order(7)
    @DisplayName("실패-2. 정상 로그인시 시도 횟수가 초기화되고 이후 잘못된 비밀번호로 5회 초과시 로그인 제한")
    @Test
    public void checkLoginAttempt_shouldResetAndBlock_whenSuccessAndExceeded() throws Exception {
      //given
      String invalidPassword = "invalid_password";
      //when

      /*잘못된 로그인 3회 시도*/
      for (int i = 0; i < 3; i++) {
        sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword, new TypeReference<>() {
        }, status().isUnauthorized());
      }

      /*정상 로그인 시도*/
      CommonResponse<LoginTokenResponse> response1 = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          PASSWORD,
          new TypeReference<>() {
          }, status().isOk());

      /*잘봇된 로그인 5회 시도*/
      for (int i = 0; i < MAX_LOGIN_ATTEMPTS; i++) {
        sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword, new TypeReference<>() {
        }, status().isUnauthorized());
      }
      CommonResponse<Object> response2 = sendLoginRequest(EMAIL_VERIFIED_USER_1, invalidPassword,
          new TypeReference<>() {
          }, status().isTooManyRequests());

      //then
      assertAll(
          () -> assertTrue(response1.isSuccess()),
          () -> assertNotNull(response1.getData().accessToken())
      );

      assertAll(
          () -> assertFalse(response2.isSuccess()),
          () -> assertThat(response2.getCode()).isEqualTo(
              ErrorCode.LOGIN_ATTEMPT_EXCEEDED.getCode())
      );
    }
  }
}
