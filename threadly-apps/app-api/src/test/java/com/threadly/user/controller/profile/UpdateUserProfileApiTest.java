package com.threadly.user.controller.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
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
 * 사용자 프로필 업데이트 관련 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UpdateUserProfileApiTest extends BaseUserProfileApiTest {

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 사용자 프로필 업데이트 요청 시 성공해야한다*/
    @DisplayName("1.사용자 프로필 업데이트 성공 검증")
    @Test
    public void updateUserProfile_shouldSuccess() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      Map<String, String> newProfileData = Map.of(
          "userId", "user_with_profile_test",
          "nickname", "newNickname",
          "statusMessage", "newStatusMessage",
          "bio", "newBio",
          "phoneNumber", "newPhoneNumber",
          "profileImageUrl", "/images/profile/usr_1.png"
      );

      //when
      /*프로필 업데이트 요청 전송*/
      CommonResponse<Void> updateProfileResponse = sendUpdateUserProfileRequest(accessToken,
          newProfileData, status().isOk());

      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER_ID, status().isOk());

      //then
      /*프로필 업데이트 요청 응답 검증*/
      assertThat(updateProfileResponse.isSuccess()).isTrue();

      /*프로필 조회 요청 응답 검증*/
      assertUserProfileResponse(getUserProfileResponse.getData(), newProfileData);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 중복된 닉네임으로 프로필 업데이트 요청 시 409 Conflict*/
    @DisplayName("1. 중복된 닉네임으로 프로필 업데이트 요청 시 409 Conflict")
    @Test
    public void updateUserProfile_shouldReturn409Conflict_whenRequestNicknameAlreadyExists()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      /*프로필 업데이트 요청 전송*/
      CommonResponse<Void> updateProfileResponse = sendUpdateUserProfileRequest(accessToken,
          USER_PROFILE, status().isConflict());

      //then
      /*프로필 업데이트 요청 응답 검증*/
      assertThat(updateProfileResponse.isSuccess()).isFalse();
      assertThat(updateProfileResponse.getCode()).isEqualTo(
          ErrorCode.USER_NICKNAME_DUPLICATED.getCode());
    }

  }
}