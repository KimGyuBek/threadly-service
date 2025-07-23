package com.threadly.auth.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static com.threadly.utils.TestConstants.PROFILE_NOT_SET_USER_1;
import static com.threadly.utils.TestConstants.USER_EMAIL_NOT_VERIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.utils.TestConstants;
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

/**
 * 로그인 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class LoginTest extends BaseApiTest {

  @Autowired
  private UserFixtureLoader userFixtureLoader;

  /**
   * 사용자 데이터 저장
   */
  @BeforeEach
  void setUp() {
    super.setUpDefaultUser();
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /* [Case #1] login - 사용자와 비밀번호가 일치하는 경우 로그인 성공해야한다  */
    @Order(1)
    @DisplayName("1. 사용자가 존재하고 비밀번호가 일치하는 경우 로그인 성공")
    @Test
    public void login_shouldSucceed_whenUserExistsAndCorrectPassword() throws Exception {
      //given
      //when
      //then
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          TestConstants.PASSWORD,
          new TypeReference<CommonResponse<LoginTokenResponse>>() {
          }, status().isOk());
      assertThat(loginResponse.getData().accessToken()).isNotNull();
      assertThat(loginResponse.getData().refreshToken()).isNotNull();
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    @Order(1)
    /* [Case #1] 로그인 실패 - 사용자가 없는 경우  */
    @DisplayName("1.  사용자가 없는 경우 실패")
    @Test
    public void login_shouldFail_whenUserNotExists() throws Exception {
      //given
      String invalidEmail = "invalid-email@test.com";

      //when
      /*로그인 요청*/
      CommonResponse<Object> loginResponse = sendLoginRequest(invalidEmail, PASSWORD,
          new TypeReference<CommonResponse<Object>>() {
          }, status().isNotFound());

      //then
      assertAll(
          () -> assertFalse(loginResponse.isSuccess()),
          () -> assertEquals(loginResponse.getCode(),
              ErrorCode.USER_NOT_FOUND.getCode())
      );
    }


    @Order(2)
    /* [Case #2] 로그인 실패 - password가 일치하지 않음 */
    @DisplayName("2. 비밀번호가 일치하지 않는 경우")
    @Test
    public void login_shouldFail_whenPasswordNotCorrect() throws Exception {
      //given
      String invalidPassword = "4321";

      //when
      /*로그인 요청*/
      CommonResponse<Object> loginResponse = sendLoginRequest(EMAIL_VERIFIED_USER_1,
          invalidPassword,
          new TypeReference<CommonResponse<Object>>() {
          }, status().isUnauthorized());

      //then
      assertAll(
          () -> assertFalse(loginResponse.isSuccess()),
          () -> assertEquals(loginResponse.getCode(),
              ErrorCode.USER_AUTHENTICATION_FAILED.getCode())
      );
    }

    @Order(3)
    /* [Case #3] 로그인 실패 - email 인증 필요 경우 */
    @DisplayName("3. 이메일 인증이 되지 않은 경우")
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

  }
}