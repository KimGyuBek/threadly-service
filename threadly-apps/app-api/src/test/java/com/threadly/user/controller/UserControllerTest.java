package com.threadly.user.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static com.threadly.utils.TestConstants.PROFILE_NOT_SET_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
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
import com.threadly.user.profile.register.UserProfileRegistrationApiResponse;
import com.threadly.user.request.ResiterUserProfileRequest;
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
  @DisplayName("사용자 초기 프로필 설정 테스트")
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class setUserProfileTest {


    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /* [Case #1] 회원가입 후 프로필 설정을 하지 않은 사용자가 프로필 설정을 요청할 경우 */
      @Order(1)
      @DisplayName("1. 프로필 설정을 하지 않은 사용자가 프로필 설정을 요청 할 경우 검증")
      @Test
      public void setUserProfile_shouldCreateOrUpdateProfile_whenAuthenticatedUserRequests()
          throws Exception {
        //given
        /*로그인 요청 전송*/
        CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
            PROFILE_NOT_SET_USER_1, PASSWORD,
            new TypeReference<CommonResponse<LoginTokenResponse>>() {
            }, status().isOk()
        );

        String accessToken = loginResponse.getData().accessToken();

        //when
        String nickname = "nickname";
        String statusMessage = "statusMessage";
        String bio = "bio";
        String phone = "1234-1234-1234";
        UserGenderType gender = UserGenderType.MALE;
        String profileImageUrl = "";
        String requestBody = generateRequestBody(
            new ResiterUserProfileRequest(
                nickname,
                statusMessage,
                bio,
                phone,
                gender,
                profileImageUrl
            )
        );

        /*프로필 초기 설정 요청 전송*/
        CommonResponse<UserProfileRegistrationApiResponse> setProfileResponse = sendPostRequest(
            requestBody,
            "/api/user/profile",
            status().isCreated(),
            new TypeReference<CommonResponse<UserProfileRegistrationApiResponse>>() {
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
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /* [Case #1] 회원가입 후 프로필 설정을 하지 않은 사용자가 프로필 설정을 요청할 경우 */
      @Order(1)
      @DisplayName("1. 이미 프로필을 설정한 사용자가 프로필 설정 요청을 한 경우")
      @Test
      public void setUserProfile_shouldReturn409Conflict_whenUserProfileExists()
          throws Exception {
        //given
        /*로그인 요청 전송*/
        CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
            EMAIL_VERIFIED_USER_1, PASSWORD,
            new TypeReference<CommonResponse<LoginTokenResponse>>() {
            }, status().isOk()
        );

        String accessToken = loginResponse.getData().accessToken();

        //when
        String nickname = "nickname";
        String statusMessage = "statusMessage";
        String bio = "bio";
        String phone = "1234-1234-1234";
        UserGenderType gender = UserGenderType.MALE;
        String profileImageUrl = "";
        String requestBody = generateRequestBody(
            new ResiterUserProfileRequest(
                nickname,
                statusMessage,
                bio,
                phone,
                gender,
                profileImageUrl
            )
        );

        /*프로필 초기 설정 요청 전송*/
        CommonResponse<UserProfileRegistrationApiResponse> setProfileResponse = sendPostRequest(
            requestBody,
            "/api/user/profile",
            status().isConflict(),
            new TypeReference<CommonResponse<UserProfileRegistrationApiResponse>>() {
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
            () -> assertThat(setProfileResponse.isSuccess()).isFalse(),
            () -> assertThat(setProfileResponse.getCode()).isEqualTo(
                ErrorCode.USER_PROFILE_ALREADY_SET.getCode())
        );
      }


      @Order(2)
      /* [Case #2] 사용자 프로필 설정을 하지 않은 상태에서 인증을 필요한 경로에 접속할 경우 403 Forbidden*/
      @DisplayName("2. 사용자 프로필을 설정하지 않은 상태에서 인증을 필요로한 경로에 접속 할 경우 403 Forbidden")
      @Test
      public void login_shouldReturn403_whenUserProfileNotSet() throws Exception {
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
  }
}