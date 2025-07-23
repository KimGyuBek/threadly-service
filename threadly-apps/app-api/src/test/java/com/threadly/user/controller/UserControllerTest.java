package com.threadly.user.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static com.threadly.utils.TestConstants.PROFILE_NOT_SET_USER_1;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.UserGenderType;
import com.threadly.user.request.CreateUserProfileRequest;
import com.threadly.user.response.UserProfileSetupApiResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * UserController Test
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UserControllerTest extends BaseApiTest {


  @BeforeEach
  void setUp() {
    super.setUpDefaultUser();
  }

  /**
   * setUserProfile() - 테스트
   *
   * @throws Exception
   */
  @Order(1)
  @DisplayName("사용자 프로필 설정 테스트")
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class setUserProfileTest {

    @Order(1)
    @DisplayName("성공-1. 인증받은 사용자가 프로필 설정을 요청할 경우")
    @Test
    public void setUserProfile_shouldCreateOrUpdateProfile_whenAuthenticatedUserRequests()
        throws Exception {
      //given
      /*로그인 요청 전송*/
      CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
          }, status().isOk()
      );

      String accessToken = loginResponse.getData().accessToken();

      //when
      String nickname = "nickname";
      String statusMessage = "statusMessage";
      String bio = "bio";
      UserGenderType gender = UserGenderType.MALE;
      String profileImageUrl = "";
      String requestBody = generateRequestBody(
          new CreateUserProfileRequest(
              nickname,
              statusMessage,
              bio,
              gender,
              profileImageUrl
          )
      );

      /*프로필 초기 설정 요청 전송*/
      CommonResponse<UserProfileSetupApiResponse> setProfileResponse = sendPostRequest(
          requestBody,
          "/api/user/profile",
          status().isCreated(),
          new TypeReference<CommonResponse<UserProfileSetupApiResponse>>() {
          },
          Map.of("Authorization", "Bearer " + accessToken)
      );

      //then
      /*로그인 응답 검증*/
      assertAll(
          () -> assertTrue(loginResponse.isSuccess()),
          () -> assertNotNull(loginResponse.getData().accessToken())
      );

      /*프로필 생성 응답 검증*/
      assertAll(
          () -> assertTrue(setProfileResponse.isSuccess()),
          () -> assertNotNull(setProfileResponse.getData().accessToken())
      );
    }
  }

  @Order(2)
  /* [Case #4] 사용자 프로필 설정을 하지 않은 상태에서 인증을 필요한 경로에 접속할 경우 403 Forbidden*/
  @DisplayName("4. 사용자 프로필이 설정되지 않은 경우")
  @Test
  public void login_shouldFail_whenUserProfileNotSet() throws Exception {
//    given
//    when
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(PROFILE_NOT_SET_USER_1,
        PASSWORD,
        new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk());

    CommonResponse response = sendGetRequest(
        loginResponse.getData().accessToken(),
        "/",
        status().isForbidden()
    );

//    then
    assertAll(
        () -> assertFalse(response.isSuccess()),
        () -> assertEquals(response.getCode(), ErrorCode.USER_PROFILE_NOT_SET.getCode())
    );
  }
}