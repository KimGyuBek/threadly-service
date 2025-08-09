package com.threadly.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.request.PasswordVerificationRequest;
import com.threadly.core.usecase.auth.token.response.TokenReissueApiResponse;
import com.threadly.core.usecase.auth.verification.response.PasswordVerificationToken;
import com.threadly.repository.TestUserRepository;
import com.threadly.adapter.persistence.core.user.request.me.ChangePasswordRequest;
import com.threadly.core.domain.user.UserStatusType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Base User Api test
 */
public abstract class BaseUserApiTest extends BaseApiTest {

  @Autowired
  private TestUserRepository testUserRepository;

  /**
   * 비밀번호 변경 요청
   *
   * @param accessToken
   * @param xVerifyToken
   * @param newPassword
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendChangePasswordRequest(String accessToken, String xVerifyToken,
      String newPassword,
      ResultMatcher expectedStatus)
      throws Exception {
    String requestBody = generateRequestBody(
        new ChangePasswordRequest(newPassword)
    );
    return
        sendPatchRequest(
            requestBody,
            "/api/me/account/password",
            expectedStatus, new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken,
                "X-Verify-Token", "Bearer " + xVerifyToken)
        );
  }

  /**
   * 내 계정 탈퇴 요청
   *
   * @return
   */
  public CommonResponse<Void> sendWithdrawMyAccountRequest(String accessToken, String xVerifyToken,
      ResultMatcher expectedStatus) throws Exception {
    return
        sendDeleteRequest(
            "", "/api/me/account", expectedStatus, new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken,
                "X-Verify-Token", "Bearer " + xVerifyToken)
        );
  }

  /**
   * 내 계정 비활성화 요청
   *
   * @return
   */
  public CommonResponse<Void> sendDeactivateMyAccountRequest(String accessToken,
      String xVerifyToken,
      ResultMatcher expectedStatus) throws Exception {
    return
        sendPatchRequest(
            "", "/api/me/account/deactivate", expectedStatus, new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken,
                "X-Verify-Token", "Bearer " + xVerifyToken)
        );
  }

  /**
   * X-Verify-Token 발급 요청
   *
   * @param accessToken
   * @return
   * @throws Exception
   */
  public String getXVerifyToken(String accessToken)
      throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    String requestBody = generateRequestBody(
        new PasswordVerificationRequest(PASSWORD)
    );

    return
        sendPostRequest(
            requestBody, "/api/auth/verify-password", status().isOk(),
            new TypeReference<CommonResponse<PasswordVerificationToken>>() {
            },
            headers
        ).getData().getVerifyToken();
  }

  /**
   * accessToken 재발급 요청
   *
   * @param refreshToken
   * @param expectedStatus
   * @return
   */
  public CommonResponse<TokenReissueApiResponse> sendReissueTokenRequest(String refreshToken,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendPostRequest("", "/api/auth/reissue",
            expectedStatus,
            new TypeReference<>() {
            }, Map.of("X-refresh-token", "Bearer " + refreshToken));
  }

  /**
   * email로 user statusType 검증
   *
   * @param email
   * @param expectedStatus
   */
  public void validateUserStatusType(String email, UserStatusType expectedStatus) {
    UserStatusType statusByEmail = testUserRepository.findStatusByEmail(
        email);
    assertThat(statusByEmail).isEqualTo(expectedStatus);
  }
}
