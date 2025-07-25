package com.threadly.user.controller.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
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
 * 사용자 프로필 조회 관련 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class GetUserProfileApiTest extends BaseUserProfileApiTest {

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 사용자와 프로필이 존재하는 경우 성공해야한다*/
    @Order(1)
    @DisplayName("1. 사용자와 프로필이 존재하는 경우 성공 검증")
    @Test
    public void getUserProfile_shouldSuccess_whenUserAndUserProfileExists() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER_ID, status().isOk());

      //then
      /*응답 검증*/
      assertUserProfileResponse(getUserProfileResponse.getData(), USER_PROFILE);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 존재하지 않은 userId로 조회한 경우 */
    @Order(1)
    @DisplayName("1. 존재하지 않는 userId로 조회한 경우 404 NotFound")
    @Test
    public void getUserProfile_shouldReturnNotFound_whenUserIdNotExists() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, "user_not_exists_id", status().isNotFound());

      //then
      /*응답 검증*/
      assertThat(getUserProfileResponse.isSuccess()).isFalse();
      assertThat(getUserProfileResponse.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
    }

    /*[Case #2]  사용자는 존재하지만 profile이 없는 경우*/
    @Order(2)
    @DisplayName("2. 사용자는 존재하지만 profile이 없는 경우")
    @Test
    public void getUserProfile_shouldReturnNotFound_whenUserProfileNotSet() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, TestConstants.PROFILE_NOT_SET_USER_1, status().isNotFound());

      //then
      /*응답 검증*/
      assertThat(getUserProfileResponse.isSuccess()).isFalse();
      assertThat(getUserProfileResponse.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
    }


    /*[Case #3]  차단된 사용자를 조회할 경우*/
    /*[Case #4]  탈퇴한 사용자를 조회할 경우 */
  }
}