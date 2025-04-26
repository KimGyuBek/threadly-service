package com.threadly.controller.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.controller.auth.request.PasswordVerificationRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Auth Controller 테스트
 */
class AuthControllerTest extends BaseApiTest {

  /**
   * 로그인 테스트
   */
  /* [Case #1] 로그인 성공  */
  /* [Case #2] 로그인 실패 - 사용자가 없는 경우  */
  @DisplayName("로그인 실패 - 사용자가 없는 경우")
  @Test
  public void login_shouldFail_whenUserNotExists() throws Exception {
    //given
    String invalidEmail = "invalid-email@test.com";

    //when
    /*로그인 요청*/
    CommonResponse<Object> loginResponse = sendLoginRequest(invalidEmail, PASSWORD,
        new TypeReference<CommonResponse<Object>>() {
        }, status().isUnauthorized());

    //then
    assertAll(
        () -> assertFalse(loginResponse.isSuccess()),
        () -> assertEquals(loginResponse.getCode(), ErrorCode.USER_AUTHENTICATION_FAILED.getCode())
    );
  }


  /* [Case #3] 로그인 실패 - password가 일치하지 않음 */
  @DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
  @Test
  public void login_shouldFail_whenPasswordNotCorrect() throws Exception {
    //given
    String invalidPassword = "4321";

    //when
    /*로그인 요청*/
    CommonResponse<Object> loginResponse = sendLoginRequest(USER_EMAIL_NOT_VERIFIED,
        invalidPassword,
        new TypeReference<CommonResponse<Object>>() {
        }, status().isUnauthorized());

    //then
    assertAll(
        () -> assertFalse(loginResponse.isSuccess()),
        () -> assertEquals(loginResponse.getCode(), ErrorCode.USER_AUTHENTICATION_FAILED.getCode())
    );
  }

  /* [Case #4] 로그인 실패 - email 인증 필요 경우 */
  @DisplayName("로그인 실패 - 이메일 인증이 되지 않은 경우")
  @Test
  public void login_shouldFail_whenEmailNotVerified() throws Exception {
//    given
//    when
    CommonResponse<Object> loginResponse = sendLoginRequest(USER_EMAIL_NOT_VERIFIED, PASSWORD,
        new TypeReference<CommonResponse<Object>>() {
        }, status().isUnauthorized());
//    then
    assertAll(
        () -> assertFalse(loginResponse.isSuccess()),
        () -> assertEquals(loginResponse.getCode(), ErrorCode.EMAIL_NOT_VERIFIED.getCode())
    );
  }

  /**
   * 로그아웃 테스트
   */
  /*[Case #1] 로그인 성공 후 로그아웃 성공*/
  @DisplayName("로그인 성공 후 로그아웃 성공")
  @Test
  public void logout_shouldSucceed_whenLoginSucceed() throws Exception {
    //given

    /*로그인 요청*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();

    /*Header 설정*/
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Authorization", "Bearer " + accessToken);

    //when
    /*로그아웃 요청 전송*/
    CommonResponse<Object> logoutResponse = sendPostRequest(
        "",
        "/api/auth/logout",
        status().isOk(),
        new TypeReference<CommonResponse<Object>>() {
        }, headers
    );

    //then
    /*로그인 응답 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken()),
        () -> assertNotNull(loginResponse.getData().refreshToken())
    );

    /*로그아웃 응답 검증*/
    assertTrue(logoutResponse.isSuccess());
  }

  /*[Case #2] 로그아웃 실패 - 토큰 오류*/
  @DisplayName("로그아웃 실패 - 토큰 오류")
  @Test
  public void logout_shouldFailed_whenLoginSucceed_andTokenInvalid() throws Exception {
    //given

    /*로그인 요청*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();
    accessToken += "abc";

    /*Header 설정*/
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Authorization", "Bearer " + accessToken);

    //when
    /*로그아웃 요청 전송*/
    CommonResponse<Object> logoutResponse = sendPostRequest(
        "",
        "/api/auth/logout",
        status().isBadRequest(),
        new TypeReference<CommonResponse<Object>>() {
        }, headers
    );

    //then
    /*로그인 응답 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken()),
        () -> assertNotNull(loginResponse.getData().refreshToken())
    );

    /*로그아웃 응답 검증*/
    assertAll(
        () -> assertFalse(logoutResponse.isSuccess()),
        () -> assertThat(logoutResponse.getCode()).isEqualTo(ErrorCode.TOKEN_INVALID.getCode())
    );
  }

  /*[Case #3] 로그아웃 실패 - 만료된 accessToken으로 요청*/
  @DisplayName("로그아웃 실패 -  만료된 accessToken으로 요청")
  @Test
  public void logout_shouldFailed_whenLoginSucceed_andTokenExpired() throws Exception {
    //given

    /*로그인 요청*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();

    /*Header 설정*/
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Authorization", "Bearer " + accessToken);

    Thread.sleep(3200);
    //when
    /*로그아웃 요청 전송*/
    CommonResponse<Object> logoutResponse = sendPostRequest(
        "",
        "/api/auth/logout",
        status().isUnauthorized(),
        new TypeReference<CommonResponse<Object>>() {
        }, headers
    );

    //then
    /*로그인 응답 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken()),
        () -> assertNotNull(loginResponse.getData().refreshToken())
    );

    /*로그아웃 응답 검증*/
    assertAll(
        () -> assertFalse(logoutResponse.isSuccess()),
        () -> assertThat(logoutResponse.getCode()).isEqualTo(ErrorCode.TOKEN_EXPIRED.getCode())
    );
  }

  /**
   * 이중인증 테스트
   */
  /*[Case #1] 이중인증 성공 - 비밀번호가 일치할 경우*/
  @DisplayName("이중인증 성공  - 비밀번호가 일치할 경우")
  @Test
  public void passwordVerificationToken_shouldReturnVerificationToken_whenValidPassword()
      throws Exception {
    //given

    /*로그인 요청*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED,
        PASSWORD,
        new TypeReference<>() {
        },
        status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();

    //when
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    String requestBody = generateRequestBody(
        new PasswordVerificationRequest(PASSWORD)
    );

    CommonResponse<PasswordVerificationToken> passwordVerificationResponse = sendPostRequest(
        requestBody, "/api/auth/verify-password", status().isOk(),
        new TypeReference<>() {
        },
        headers
    );

    //then
    /*login response 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken())
    );

    /*passwordVerification respones 검증*/
    assertAll(
        () -> assertTrue(passwordVerificationResponse.isSuccess()),
        () -> assertNotNull(passwordVerificationResponse.getData().getVerifyToken())
    );

  }

  /*[Case #2] 이중인증 실패 - 비밀번호가 일치하지 않을 경우*/
  @DisplayName("이중인증 실패  - 비밀번호가 일치하지 않는 경우")
  @Test
  public void passwordVerificationToken_shouldFailed_whenInValidPassword()
      throws Exception {
    //given

    /*로그인 요청*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED,
        PASSWORD,
        new TypeReference<>() {
        },
        status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();

    //when
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    String invalidPassword = "4321";

    String requestBody = generateRequestBody(
        new PasswordVerificationRequest(invalidPassword)
    );

    CommonResponse<?> passwordVerificationResponse = sendPostRequest(
        requestBody, "/api/auth/verify-password", status().isUnauthorized(),
        new TypeReference<>() {
        },
        headers
    );

    //then
    /*login response 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken())
    );

    /*passwordVerification respones 검증*/
    assertAll(
        () -> assertFalse(passwordVerificationResponse.isSuccess()),
        () -> assertThat(
            passwordVerificationResponse.getCode()
                .equals(ErrorCode.USER_AUTHENTICATION_FAILED.getCode()))
    );

  }

}