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
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * auth - logout 시나리오 테스트
 */
public class LogoutScenarioTest extends BaseApiTest {


  /*로그아웃 시나리오 테스트*/
  /*[Case #1] 로그인 -> 로그아웃 -> '/'접속시 불가 */
  @DisplayName("로그인 후 '/' 접속 -> 성공")
  @Test
  @Transactional
  public void accessProtectedResource_shouldFail_afterLogout() throws Exception {
    //given

    /*1. 로그인 요청 전송*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED_1, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk());

    String accessToken = loginResponse.getData().accessToken();

    /*2. 로그아웃 요청 전송*/
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    CommonResponse<Object> logoutResponse = sendLogoutRequest(
        new TypeReference<CommonResponse<Object>>() {
        },
        status().isOk(),
        headers
    );

    /* '/' 경로로 접속*/
    CommonResponse response = sendGetRequest(
        accessToken,
        "/",
        status().isBadRequest()
    );

    //then
    /*login response 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken())
    );

    /*logout response 검증*/
    assertTrue(logoutResponse.isSuccess());

    /* '/' 접속 검증*/
    assertAll(
        () -> assertFalse(response.isSuccess()),
        () -> assertThat(response.getCode()).isEqualTo(ErrorCode.TOKEN_INVALID.getCode())
    );

  }


}
