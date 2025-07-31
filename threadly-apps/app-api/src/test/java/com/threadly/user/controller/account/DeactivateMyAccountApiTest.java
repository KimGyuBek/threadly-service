package com.threadly.user.controller.account;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.BaseUserApiTest;
import com.threadly.user.UserStatusType;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 내 계정 비활성화 API 테스트
 */
@DisplayName("내 계정 비활성화 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class DeactivateMyAccountApiTest extends BaseUserApiTest {


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 유효한 userId로 비활성화 요청 시 상태 변경 검증*/
    @Order(1)
    @DisplayName("1. 내 계정 비활성화 성공 시 상태 변경 검증")
    @Test
    public void deactivateMyAccount_shouldSuccess_whenUserIdExists() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이중인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);
      /*탈퇴 요청*/
      CommonResponse<Void> deactivateMyAccountResponse = sendDeactivateMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      //then
      /*응답 검증*/
      assertThat(deactivateMyAccountResponse.isSuccess()).isTrue();

      /*user statusType 검증*/
      validateUserStatusType(EMAIL_VERIFIED_USER_1, UserStatusType.INACTIVE);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 계정 비활성화 후 로그인이 불가능한지 검증*/
    @Order(1)
    @DisplayName("1. 계정 비활성화 후 로그인이 불가능한지 검증")
    @Test
    public void deactivateMyAccount_shouldReturn403Forbidden_whenUserLoginAfterUserDeactivate() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이중인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);

      /*탈퇴 요청*/
      sendDeactivateMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      /*로그인 요청*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          TestConstants.PASSWORD,
          new TypeReference<>() {
          }, status().isForbidden());

      //then
      /*응답 검증*/
      assertThat(loginResponse.isSuccess()).isFalse();
      assertThat(loginResponse.getCode()).isEqualTo(ErrorCode.USER_INACTIVE.getCode());
    }

    /*[Case #2] 계정 비활성화 후  인증이 필요한 경로로 접근이 불가능한지 검증*/
    @Order(2)
    @DisplayName("2. 계정 비활성화 후 인증을 필요로한 경로로 접근이 불가능한지 검증")
    @Test
    public void deactivateMyAccount_shouldReturn400BadRequest_whenAccessAuthorizedPathAfterUserDeactivate() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이중인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);

      /*탈퇴 요청*/
      sendDeactivateMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      /*로그인 요청*/
      CommonResponse response = sendGetRequest(accessToken, "/", status().isBadRequest());

      //then
      /*응답 검증*/
      assertThat(response.isSuccess()).isFalse();
      assertThat(response.getCode()).isEqualTo(ErrorCode.TOKEN_INVALID.getCode());
    }
    /*[Case #3] 계정 비활성화 후  인증이 필요한 경로로 접근이 불가능한지 검증*/
    @Order(3)
    @DisplayName("3. 이중인증 없이 비활성화를 요청 할 경우 400 BadRequest")
    @Test
    public void deactivateMyAccount_shouldReturn400BadRequest_whenDeactivateWithout2FA() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*탈퇴 요청*/
      CommonResponse<Void> deactivateResponse = sendDeactivateMyAccountRequest(accessToken,
          null,
          status().isBadRequest());
      //then
      /*응답 검증*/
      assertThat(deactivateResponse.isSuccess()).isFalse();
      assertThat(deactivateResponse.getCode()).isEqualTo(ErrorCode.SECOND_VERIFICATION_FAILED.getCode());
    }
  }
}


