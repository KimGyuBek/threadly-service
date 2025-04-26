package com.threadly.scenario.auth;

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
import com.threadly.controller.auth.request.UserLoginRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * auth - login 시나리오 테스트
 */
public class LoginScenarioTest extends BaseApiTest {


  /* 로그인 시나리오 테스트*/
  /*
   * [Case #1]
   * 로그인 성공 -> "/" 접속 -> 요청 성공 (정상 동작)
   * */
  @DisplayName("로그인 후 '/' 접속 -> 성공")
  @Test
  @Transactional
  public void accessProtectedResource_shouldSucceed_whenLoggedIn() throws Exception {
    //given

    /*로그인 요청 body*/
//    String requestJson = getLoginRequest(USER_EMAIL_VERIFIED, "1234");
    String loginRequestBody = generateRequestBody(
        UserLoginRequest.builder()
            .email(USER_EMAIL_VERIFIED)
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
  @DisplayName("로그인 후 만료된 accessToken으로  접속'/' -> TLY3000 -> 재접속 -> 성공")
  @Test
  public void accessProtectedResource_sholudReturnUnAuthorized_whenAccessTokenNotExists()
      throws Exception {
    //given
    //when
    /*로그인*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED,
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
  @DisplayName("로그인 후 만료된 accessToken으로  접속'/' -> TLY3000")
  @Test
  public void accessProtectedResource_shouldSucceed_afterAccessTokenExpired_whenReissueAccessToken()
      throws Exception {
    //given
    //when

    /*로그인*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
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
        }, Map.of("X-refresh-token", refreshToken)
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

  /* [case #4] 로그인시 재발급되는 토큰 비교 검증*/
  @Test
  public void verifyLoginToken_shouldDifferent_whenForEachLogin() throws Exception {
    //given

    List<String> accessTokenList = new ArrayList<>();
    List<String> refreshTokenList = new ArrayList<>();

    CommonResponse<LoginTokenResponse> loginResponse;

    for (int i = 0; i < 50; i++) {
      loginResponse = sendLoginRequest(
          USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<>() {
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

}
