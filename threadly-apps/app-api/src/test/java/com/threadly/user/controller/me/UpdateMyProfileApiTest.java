package com.threadly.user.controller.me;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.testsupport.fixture.users.UserProfileImageFixtureLoader;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 내 프로필 업데이트 관련 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UpdateMyProfileApiTest extends BaseMyProfileApiTest {

  @Autowired
  private UserProfileImageFixtureLoader userProfileImageFixtureLoader;

  @BeforeEach
  public void setup() {
    userFixtureLoader.load(
        "/users/profile/images/update/user.json"
    );
    userProfileImageFixtureLoader.load(
        "/users/profile/images/update/profile-images.json"
    );
  }


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 기존 프로필 이미지가 존재하는 경우 기존 프로필 이미지 삭제*/
    @Order(1)
    @DisplayName("1. 기존 프로필 이미지가 존재하고 기존 프로필 이미지를 삭제하는 경우 검증")
    @Test
    public void updateMyProfile_shouldDeleteExistingProfileImage_whenProfileImageIdIsNull()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_WITH_PROFILE_IMAGE_EMAIL);

      //when
      /*프로필 업데이트 요청*/
      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          null
      );

      /*프로필 업데이트 요청*/
      sendUpdateMyProfileRequest(
          accessToken,
          newProfileData,
          status().isOk()
      );

      /*내 프로필 정보 조회 후 검증*/
      validateMyProfileDetailsResponseAfterUpdate(accessToken, USER_WITH_PROFILE_IMAGE_ID,
          newProfileData);
    }

    /*[Case #2] 기존 프로필 이미지가 존재하는 경우 새 프로필 이미지로 업데이트*/
    @Order(2)
    @DisplayName("2. 기존 프로필 이미지가 존재하고 새로운 프로필 이미지를 설정하는 경우 검증")
    @Test
    public void updateMyProfile_shouldUpdateProfileImage_whenProfileImageIdIsDifferent()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_WITH_PROFILE_IMAGE_EMAIL);

      //when
      /*프로필 업데이트 요청*/
      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          TEMPORARY_IMAGE.get("userProfileImageId")
      );

      /*프로필 업데이트 요청*/
      sendUpdateMyProfileRequest(
          accessToken,
          newProfileData,
          status().isOk()
      );

      /*내 프로필 정보 조회 후 검증*/
      validateMyProfileDetailsResponseAfterUpdate(accessToken, USER_WITH_PROFILE_IMAGE_ID,
          newProfileData);
    }

    /*[Case #3] 기존 프로필 이미지가 존재하는 경우 프로필 이미지 삭제*/
    @Order(3)
    @DisplayName("3. 기존 프로필 이미지가 존재하고 기존 프로필 이미지를 유지하는 경우 검증")
    @Test
    public void updateMyProfile_shouldKeepExistingProfileImage_whenImageIdIsSame()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_WITH_PROFILE_IMAGE_EMAIL);

      //when
      /*프로필 업데이트 요청*/
      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          CONFIRMED_IMAGE.get("userProfileImageId")
      );

      /*프로필 업데이트 요청*/
      sendUpdateMyProfileRequest(
          accessToken,
          newProfileData,
          status().isOk()
      );

      /*내 프로필 정보 조회 후 검증*/
      validateMyProfileDetailsResponseAfterUpdate(accessToken, USER_WITH_PROFILE_IMAGE_ID,
          newProfileData);
    }

    /*[Case #4] 기존 프로필 이미지가 존재하지 않는 경우 새 프로필 이미지로 설정*/
    @Order(4)
    @DisplayName("4. 기존 프로필 이미지가 존재하지 않고 새로운 프로필 이미지를 설정하는 경우 검증")
    @Test
    public void updateMyProfile_shouldSetNewProfileImage_whenNoExistingProfileImageAndProfileImageIdIsGiven()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(NO_PROFILE_IMAGE_USER_EMAIL);

      //when
      /*프로필 업데이트 요청*/
      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          TEMPORARY_IMAGE.get("userProfileImageId")
      );

      /*프로필 업데이트 요청*/
      sendUpdateMyProfileRequest(
          accessToken,
          newProfileData,
          status().isOk()
      );

      /*내 프로필 정보 조회 후 검증*/
      validateMyProfileDetailsResponseAfterUpdate(accessToken, NO_PROFILE_IMAGE_USER_ID,
          newProfileData);
    }

    /*[Case #5] 기존 프로필 이미지가 존재하지 않는 경우 유지*/
    @DisplayName("5. 기존 프로필 이미지가 존재하지 않고 새 프로필을 설정하지 않는 경우 검증")
    @Test
    public void updateMyProfile_shouldDoNotingToProfileImage_whenNoExistingImageAndImageIdIsNull()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          null
      );

      //when
      /*프로필 업데이트 요청 전송*/
      CommonResponse<Void> updateProfileResponse = sendUpdateMyProfileRequest(accessToken,
          newProfileData, status().isOk());

      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER_ID, status().isOk());

      //then
      /*프로필 조회*/
      CommonResponse<GetMyProfileDetailsApiResponse> getMyProfileDetailsResponse = sendGetMyProfileDetailsRequest(
          accessToken, status().isOk());

      /*프로필 조회 요청 응답 검증*/
      validateMyProfileDetailsResponse(getMyProfileDetailsResponse.getData(), USER_ID,
          newProfileData);

      /*내 프로필 정보 조회 후 검증*/
      validateMyProfileDetailsResponseAfterUpdate(accessToken, USER_ID, newProfileData);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 중복된 닉네임으로 프로필 업데이트 요청 시 409 Conflict*/
    @Order(1)
    @DisplayName("1. 중복된 닉네임으로 프로필 업데이트 요청 시 409 Conflict")
    @Test
    public void updateUserProfile_shouldReturn409Conflict_whenRequestNicknameAlreadyExists()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      //then
      /*프로필 업데이트 요청 후 실패 응답 검증*/
      validateUpdateMyProfileFailureResponse(accessToken, USER_PROFILE,
          ErrorCode.USER_NICKNAME_DUPLICATED, status().isConflict());
    }

    /*[Case #2] 존재하지 않는 profileImageId로 요청 시 404 Not Found*/
    @Order(2)
    @DisplayName("2. 존재하지 않는 profileImageId로 업데이트 요청 시 404 Not Found")
    @Test
    public void updateMyProfile_shouldFail_whenProfileImageIdIsInvalid()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          "profile_image_invalid_id"
      );

      //when
      //then
      /*프로필 업데이트 요청 후 실패 응답 검증*/
      validateUpdateMyProfileFailureResponse(accessToken, newProfileData,
          ErrorCode.USER_PROFILE_IMAGE_NOT_EXISTS, status().isNotFound());
    }

    /*[Case #3] DELETED 상태의 profileImageId로 요청 시 404 Not Found*/
    @Order(3)
    @DisplayName("3. DELETED 상태의 profileImageId로 업데이트 요청 시 404 Not Found")
    @Test
    public void updateMyProfile_shouldReturn404NotFound_whenProfileImageIdStatusIsDeleted()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      Map<String, String> newProfileData = generateNewProfileData(
          "newNickname",
          "newStatusMessage",
          "newBio",
          "newPhoneNumber",
          DELETED_IMAGE.get("userProfileImageId")
      );

      //when
      //then
      /*프로필 업데이트 요청 후 실패 응답 검증*/
      validateUpdateMyProfileFailureResponse(accessToken, newProfileData,
          ErrorCode.USER_PROFILE_IMAGE_NOT_EXISTS, status().isNotFound());
    }

  }
}