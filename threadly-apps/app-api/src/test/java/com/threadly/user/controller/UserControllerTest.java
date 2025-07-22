package com.threadly.user.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
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
      CommonResponse<UserProfileSetupApiResponse> response = sendPostRequest(
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

      assertAll(
          () -> assertTrue(response.isSuccess()),
          () -> assertNotNull(response.getData().accessToken())
      );
//      /*프로필 응답 검증*/
//      assertAll(
//          () -> assertTrue(response.isSuccess()),
//          () -> assertThat(response.getData().nickname()).isEqualTo(nickname),
//          () -> assertThat(response.getData().statusMessage()).isEqualTo(statusMessage),
//          () -> assertThat(response.getData().bio()).isEqualTo(bio),
//          () -> assertThat(response.getData().gender()).isEqualTo(gender.name()),
//          () -> assertThat(response.getData().profileImageUrl()).isEqualTo(profileImageUrl)
//      );
    }
  }
}