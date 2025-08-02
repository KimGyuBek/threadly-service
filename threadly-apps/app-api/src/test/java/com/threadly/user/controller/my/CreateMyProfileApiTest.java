package com.threadly.user.controller.my;

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
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenApiResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.UserGenderType;
import com.threadly.user.profile.register.RegisterMyProfileApiResponse;
import java.util.Map;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 사용자 프로필 생성 관련 테스트
 */
@DisplayName("사용자 프로필 생성 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class CreateMyProfileApiTest extends BaseMyProfileApiTest {

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
      CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(
          PROFILE_NOT_SET_USER_1, PASSWORD,
          new TypeReference<>() {
          }, status().isOk()
      );

      String accessToken = loginResponse.getData().accessToken();

      //when
      Map<String, String> profileData = Map.of(
          "nickname", "nickname",
          "statusMessage", "statusMessage",
          "bio", "bio",
          "phone", "1234-1234-1234",
          "gender", UserGenderType.MALE.name(),
          "profileImageUrl", "/");


      /*프로필 초기 설정 요청 전송*/
      CommonResponse<RegisterMyProfileApiResponse> setProfileResponse = sendSetMyProfileRequest(
          accessToken, profileData, status().isCreated()
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
      CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(
          EMAIL_VERIFIED_USER_1, PASSWORD,
          new TypeReference<CommonResponse<LoginTokenApiResponse>>() {
          }, status().isOk()
      );

      String accessToken = loginResponse.getData().accessToken();

      //when
      Map<String, String> profileData = Map.of(
          "nickname", "nickname",
          "statusMessage", "statusMessage",
          "bio", "bio",
          "phone", "1234-1234-1234",
          "gender", UserGenderType.MALE.name(),
          "profileImageUrl", "/");


      /*프로필 초기 설정 요청 전송*/
      CommonResponse<RegisterMyProfileApiResponse> setProfileResponse = sendSetMyProfileRequest(
          accessToken, profileData, status().isConflict()
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

    /* [Case #2] 프로필 설정 시 중복된 닉네임인 경우 409 Conflict*/
    @Order(2)
    @DisplayName("2. 중복된 닉네임으로 수정 요청을 보냈을 경우 409 Conflict")
    @Test
    public void setUserProfile_shouldReturn409Conflict_whenNicknameAlreadyExists()
        throws Exception {
      //given
      /*로그인 요청 전송*/
      CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(
          PROFILE_NOT_SET_USER_1, PASSWORD,
          new TypeReference<CommonResponse<LoginTokenApiResponse>>() {
          }, status().isOk()
      );

      String accessToken = loginResponse.getData().accessToken();

      //when

      /*프로필 초기 설정 요청 전송*/
      CommonResponse<RegisterMyProfileApiResponse> setProfileResponse = sendSetMyProfileRequest(
          accessToken, USER_PROFILE, status().isConflict()
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
              ErrorCode.USER_NICKNAME_DUPLICATED.getCode())
      );
    }
  }

  /* [Case #1] 사용자 프로필 설정을 하지 않은 상태에서 인증을 필요한 경로에 접속할 경우 403 Forbidden*/
  @Order(2)
  @DisplayName(" 사용자 프로필을 설정하지 않은 상태에서 인증을 필요로한 경로에 접속 할 경우 403 Forbidden")
  @Test
  public void login_shouldReturn403_whenUserProfileNotSet() throws Exception {
//    given
//    when
    CommonResponse<LoginTokenApiResponse> loginResponse = sendLoginRequest(PROFILE_NOT_SET_USER_1,
        PASSWORD,
        new TypeReference<CommonResponse<LoginTokenApiResponse>>() {
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