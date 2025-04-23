package com.threadly.controller.auth;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthControllerTest extends BaseApiTest {

  private String email = "auth-controller-user1@test.com";
  private String password = "1234";

  /* login 테스트  */
  /* [Case #1] 로그인 성공  */
  /* [Case #2] 로그인 실패 - 사용자가 없는 경우  */
  @DisplayName("로그인 실패 - 사용자가 없는 경우")
  @Test
  public void login_shouldFail_whenUserNotExists() throws Exception {
    //given
    String invalidEmail = "invalid-email@test.com";

    //when
    /*로그인 요청*/
    CommonResponse<Object> loginResponse = sendLoginRequest(invalidEmail, password,
        new TypeReference<CommonResponse<Object>>() {
        });

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
    CommonResponse<Object> loginResponse = sendLoginRequest(email, invalidPassword,
        new TypeReference<CommonResponse<Object>>() {
        });

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
    CommonResponse<Object> loginResponse = sendLoginRequest(email, password,
        new TypeReference<CommonResponse<Object>>() {
        });
//    then
    assertAll(
        () -> assertFalse(loginResponse.isSuccess()),
        () -> assertEquals(loginResponse.getCode(), ErrorCode.EMAIL_NOT_VERIFIED.getCode())
    );
  }


}