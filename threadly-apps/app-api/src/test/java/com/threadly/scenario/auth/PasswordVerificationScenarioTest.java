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
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.auth.request.PasswordVerificationRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * auth - password Verification 테스트
 */
public class PasswordVerificationScenarioTest extends BaseApiTest {

  @BeforeEach
  void setUp() throws Exception {
    super.setUpDefaultUser();
  }

  @Nested
  @DisplayName("사용자 이중 인증 시라니오 테스트")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class verifyPassword {

    /*사용자 이중인증 시나리오 테스트*/
    /*[Case #1] 로그인 -> 사용자 정보 업데이트 접근 -> 실패 -> 이중인증 성공 -> 사용자 정보 업데이트 재접속 -> 성공*/
    @Order(1)
    @DisplayName("성공-1. 이중 인증 성공 후 사용자 정보 업데이트 경로 접속")
    @Test
    @Transactional
    public void accessProtectedResource_shouldSucceed_afterPasswordVerification() throws Exception {
      //given
//    Thread.sleep(3000);

      //when

      /*1. 로그인 요청 전송*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, PASSWORD, new TypeReference<>() {
          }, status().isOk());

      String accessToken = loginResponse.getData().accessToken();

      /*2. /users/update/password 접속*/
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + accessToken);

      CommonResponse<Object> response1 = sendPostRequest(
          "",
          "/api/users/update/password",
          status().isBadRequest(),
          new TypeReference<>() {
          },
          headers
      );

      /*3. 비밀번호 이중 인증 요청 */
      String requestBody = generateRequestBody(
          new PasswordVerificationRequest(PASSWORD)
      );
      CommonResponse<PasswordVerificationToken> passwordVerificationResponse = sendPostRequest(
          requestBody, "/api/auth/verify-password", status().isOk(),
          new TypeReference<>() {
          },
          headers
      );
      String verifyToken = passwordVerificationResponse.getData().getVerifyToken();

      /*4. /users/update/password 재접속*/
      headers.put("X-Verify-Token", "Bearer " + verifyToken);
      CommonResponse<Object> response2 = sendPostRequest(
          "",
          "/api/users/update/password",
          status().isOk(),
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

      /*response 1 검증*/
      assertFalse(response1.isSuccess());
      assertThat(response1.getCode()).isEqualTo(ErrorCode.SECOND_VERIFICATION_FAILED.getCode());

      /*비밀번호 이중 인증 응답 검증*/
      assertAll(
          () -> assertTrue(passwordVerificationResponse.isSuccess()),
          () -> assertNotNull(passwordVerificationResponse.getData().getVerifyToken())
      );

      /*response2 검증*/
      assertTrue(response2.isSuccess());

    }

    /*[Case #2] 로그인 -> 사용자 정보 업데이트 접근 -> 실패 -> 이중인증 성공 -> 사용자 정보 업데이트 재접속 -> 성공
     * -> X-Verification-token 만료 후 접속 -> 실패
     * */
    @Order(2)
    @DisplayName("실패-1. X-Verification-token 만료후 접속")
    @Test
    @Transactional
    public void accessProtectedResource_shouldFail_afterXVerificationTokenExpired()
        throws Exception {
      //given
      //when

      /*1. 로그인 요청 전송*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, PASSWORD, new TypeReference<>() {
          }, status().isOk());

      String accessToken = loginResponse.getData().accessToken();

      /*2. /users/update/password 접속*/
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + accessToken);

      CommonResponse<Object> response1 = sendPostRequest(
          "",
          "/api/users/update/password",
          status().isBadRequest(),
          new TypeReference<>() {
          },
          headers
      );

      /*3. 비밀번호 이중 인증 요청 */
      String requestBody = generateRequestBody(
          new PasswordVerificationRequest(PASSWORD)
      );
      CommonResponse<PasswordVerificationToken> passwordVerificationResponse = sendPostRequest(
          requestBody, "/api/auth/verify-password", status().isOk(),
          new TypeReference<>() {
          },
          headers
      );
      String verifyToken = passwordVerificationResponse.getData().getVerifyToken();

      /*4. /users/update/password 재접속*/
      headers.put("X-Verify-Token", "Bearer " + verifyToken);
      CommonResponse<Object> response2 = sendPostRequest(
          "",
          "/api/users/update/password",
          status().isOk(),
          new TypeReference<>() {
          },
          headers
      );

      /* X-Verificatoin-token 만료 후 재접속*/
      Thread.sleep(3500);
      CommonResponse<Object> response3 = sendPostRequest(
          "",
          "/api/users/update/password",
          status().isUnauthorized(),
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

      /*response 1 검증*/
      assertFalse(response1.isSuccess());
      assertThat(response1.getCode()).isEqualTo(ErrorCode.SECOND_VERIFICATION_FAILED.getCode());

      /*비밀번호 이중 인증 응답 검증*/
      assertAll(
          () -> assertTrue(passwordVerificationResponse.isSuccess()),
          () -> assertNotNull(passwordVerificationResponse.getData().getVerifyToken())
      );

      /*response2 검증*/
      assertTrue(response2.isSuccess());

      /*response3 검증*/
      assertAll(
          () -> assertFalse(response3.isSuccess()),
          () -> assertThat(response3.getCode()).isEqualTo(ErrorCode.TOKEN_EXPIRED.getCode())
      );
    }
  }
}
