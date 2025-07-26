package com.threadly.user.controller;

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
 * 사용자 탈퇴 관련 테스트
 */
@DisplayName("사용자 탈퇴 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class WithdrawUserApiTest extends BaseUserApiTest {


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
      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse = sendWithDrawUserRequest(accessToken,
          status().isOk());

      //then
      /*응답 검증*/
      assertThat(withdrawUserResponse.isSuccess()).isTrue();

      /*user statusType 검증*/
      validateUserStatusType(EMAIL_VERIFIED_USER_1, UserStatusType.DELETED);
    }


    /*[Case #2] 이미 탈퇴 처리된 userId로 탈퇴 요청시 멱등한지 검증*/
    @Order(2)
    @DisplayName("2. 이미 탈퇴 처리된 userId로 탈퇴 요청 시 멱등해야한다")
    @Test
    public void withdrawUser_shouldIdempotent_whenUserIdAlreadyWithdraw() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse1 = sendWithDrawUserRequest(accessToken,
          status().isOk());

      /*탈퇴 요청 */
      CommonResponse<Void> withdrawUserResponse2 = sendWithDrawUserRequest(accessToken,
          status().isOk());

      //then
      /*응답 검증*/
      assertThat(withdrawUserResponse1.isSuccess()).isTrue();
      assertThat(withdrawUserResponse2.isSuccess()).isTrue();

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
    public void withdrawUser_shouldFail_whenUserWithdraw() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

      //when
      /*탈퇴 요청*/
      CommonResponse<Void> withdrawUserResponse1 = sendWithDrawUserRequest(accessToken,
          status().isOk());

      /*로그인*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          TestConstants.PASSWORD,
          new TypeReference<CommonResponse<LoginTokenResponse>>() {
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
  }
}


