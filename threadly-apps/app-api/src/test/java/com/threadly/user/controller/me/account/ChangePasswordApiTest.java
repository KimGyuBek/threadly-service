package com.threadly.user.controller.me.account;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.user.BaseUserApiTest;
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
 * 사용자 비밀번호 변경 관련 API 테스트
 */
@DisplayName("사용자 비밀번호 변경 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ChangePasswordApiTest extends BaseUserApiTest {

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    @DisplayName("1. 정상적인 비밀번호 변경 요청 성공 검증")
    @Test
    public void changePasswordApiTest_shouldSuccess() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      String newPassword = "newPassword";
      String xVerifyToken = getXVerifyToken(accessToken);

      //when
      /*비밀번호 변경 요청*/
      CommonResponse<Void> voidCommonResponse = sendChangePasswordRequest(accessToken, xVerifyToken,
          newPassword, status().isOk());

      //then
      /*기존 비밀번호로 로그인 요청*/
      CommonResponse<LoginTokenApiResponse> loginResponse1 = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, TestConstants.PASSWORD,
          new TypeReference<>() {
          }, status().isUnauthorized());

      /*새로 변경한 비밀번호로 로그인 요청*/
      CommonResponse<LoginTokenApiResponse> loginResponse2 = sendLoginRequest(

          EMAIL_VERIFIED_USER_1, newPassword,
          new TypeReference<>() {
          }, status().isOk());


      /*응답 검증*/
      assertThat(loginResponse1.isSuccess()).isFalse();
      assertThat(loginResponse1.getCode()).isEqualTo(
          ErrorCode.USER_AUTHENTICATION_FAILED.getCode());

      assertThat(loginResponse2.isSuccess()).isTrue();
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Cae #1] 이중인증 없이 비밀번호 요청 시 실패 검증*/
    @DisplayName("1. 이중인증 없이 비밀번호 요청 시 실패 검증")
    @Test
    public void changePasswordApiTest_shouldReturn400BadRequest_whenChangePasswordWithout2FA()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      String newPassword = "newPassword";

      //when
      /*비밀번호 변경 요청*/
      CommonResponse<Void> changePasswordResponse = sendChangePasswordRequest(accessToken, null,
          newPassword, status().isBadRequest());

      //then
      /*응답 검증*/
      assertThat(changePasswordResponse.isSuccess()).isFalse();
      assertThat(changePasswordResponse.getCode()).isEqualTo(
          ErrorCode.SECOND_VERIFICATION_FAILED.getCode());
    }

  }


}
