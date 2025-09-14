package com.threadly.user.controller.me.account;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;
import com.threadly.core.port.auth.in.token.response.TokenReissueApiResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.user.BaseUserApiTest;
import com.threadly.core.domain.user.UserStatusType;
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
 * 내 계정 탈퇴 API 테스트
 */
@DisplayName("내 계정 탈퇴 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class WithdrawMyAccountApiTest extends BaseUserApiTest {


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 유효한 userId로 탈퇴 요청 시 상태 변경 검증*/
    @Order(1)
    @DisplayName("1. 회원 탈퇴 성공 시 상태 변경 검증")
    @Test
    public void withdrawUser_shouldSuccess_whenUserIdExists() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이중인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);

      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse = sendWithdrawMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      //then
      /*응답 검증*/
      assertThat(withdrawUserResponse.isSuccess()).isTrue();

      /*user statusType 검증*/
      validateUserStatusType(EMAIL_VERIFIED_USER_1, UserStatusType.DELETED);
    }


  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 회원 탈퇴 후 로그인시 403 Forbidden, 인증이 필요한 경로로 접속 시 400  BadRequest 응답 검증 */
    @Order(1)
    @DisplayName("1. 회원 탈퇴 후 로그인 및 jwt 인증 불가 검증")
    @Test
    public void withdrawUser_shouldReturn400BadRequest_whenUserWithdraw() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*이중인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);

      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse1 = sendWithdrawMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      /*로그인*/
      CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          TestConstants.PASSWORD,
          new TypeReference<>() {
          }, status().isForbidden());

      /*인증 필요 경로로 접근*/
      CommonResponse<Void> response = sendGetRequest(accessToken, "/",
          status().isBadRequest(), new TypeReference<>() {
          });

      //then
      /*응답 검증*/
      assertThat(withdrawUserResponse1.isSuccess()).isTrue();

      assertThat(loginResponse.isSuccess()).isFalse();
      assertThat(loginResponse.getCode()).isEqualTo(ErrorCode.USER_ALREADY_DELETED.getCode());

      assertThat(response.isSuccess()).isFalse();
      assertThat(response.getCode()).isEqualTo(ErrorCode.TOKEN_INVALID.getCode());

    }

    /*[Case #2] 이중인증 없이 회원 탈퇴 요청 시  응답 검증 */
    @Order(2)
    @DisplayName("2. 이중인증 없이 회원 탈퇴 요청 시 400 BadRequest")
    @Test
    public void withdrawUser_shouldReturn400BadRequest_whenXVerifyTokenMissing() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse = sendWithdrawMyAccountRequest(accessToken, null,
          status().isBadRequest());

      //then
      /*응답 검증*/
      assertThat(withdrawUserResponse.isSuccess()).isFalse();
      assertThat(withdrawUserResponse.getCode()).isEqualTo(
          ErrorCode.SECOND_VERIFICATION_FAILED.getCode());
    }

    /*[Case #3] 회원 탈퇴 후 토큰 재발급이 불가능한지 검증 */
    @Order(3)
    @DisplayName("3. 회원 탈퇴 후 토큰 재발급이 불가능한지 검증")
    @Test
    public void withdrawUser_shouldReturn400BadRequest_whenReissueAccessTokenAfterWithdraw()
        throws Exception {
      //given

      /*로그인*/
      CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          TestConstants.PASSWORD, new TypeReference<>() {
          },
          status().isOk());

      String accessToken = loginResponse.getData().accessToken();
      //when

      /*이중 인증 요청*/
      String xVerifyToken = getXVerifyToken(accessToken);
      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse = sendWithdrawMyAccountRequest(accessToken,
          xVerifyToken,
          status().isOk());

      /*토큰 재발급 요청*/
      CommonResponse<TokenReissueApiResponse> reissueTokenResponse = sendReissueTokenRequest(
          loginResponse.getData().refreshToken(), status().isBadRequest());

      //then
      /*응답 검증*/
      assertThat(reissueTokenResponse.isSuccess()).isFalse();
      assertThat(reissueTokenResponse.getCode()).isEqualTo(ErrorCode.TOKEN_MISSING.getCode());
    }
  }
}


