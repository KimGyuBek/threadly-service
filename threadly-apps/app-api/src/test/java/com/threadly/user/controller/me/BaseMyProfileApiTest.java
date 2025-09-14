package com.threadly.user.controller.me;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;
import com.threadly.user.request.me.RegisterUserProfileRequest;
import com.threadly.user.request.me.UpdateMyProfileRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * 사용자 프로필 관련 Base 테스트
 */
public abstract class BaseMyProfileApiTest extends BaseApiTest {

  @Autowired
  public UserFixtureLoader userFixtureLoader;

  @BeforeEach
  void setUp() {
    userFixtureLoader.load(
        "/users/profile/user.json"
    );
  }

  //userId
  public static final String USER_ID = "user_with_profile_test";
  public static final String USER2_ID = "user_with_profile_test2";

  //user email
  public static final String USER_EMAIL = "user_with_profile_test@threadly.com";
  public static final String USER2_EMAIL = "user_with_profile_test2@threadly.com";

  //user password
  public static final String USER_PASSWORD = "1234";

  //user profile
  public static final Map<String, String> USER_PROFILE = Map.of(
      "userId", "user_with_profile_test",
      "nickname", "usr1_nickname",
      "statusMessage", "상태 메세지",
      "bio", "나는 사용자이다",
      "gender", "MALE",
      "profileType", "USER",
      "phone", "010-1111-1111"
  );

  public static final Map<String, String> USER2_PROFILE = Map.of(
      "userId", "user_with_profile_test2",
      "nickname", "usr1_nickname2",
      "statusMessage", "상태 메세지",
      "bio", "나는 사용자이다2",
      "gender", "MALE",
      "profileType", "USER",
      "phone", "010-2222-2222"
  );

  /*프로필 이미지가 있는 userId*/
  public static final String USER_WITH_PROFILE_IMAGE_ID = "user_with_profile_image";

  /*프로필 이미지가 없는 userId*/
  public static final String NO_PROFILE_IMAGE_USER_ID = "no_profile_image_user";

  /*프로필 이미지가 있는 email*/
  public static final String USER_WITH_PROFILE_IMAGE_EMAIL = "user_with_profile_image@threadly.com";

  /*프로필 이미지가 없는 email*/
  public static final String NO_PROFILE_IMAGE_USER_EMAIL = "no_profile_image_user@threadly.com";

  /*TEMPORARY 이미지 데이터*/
  public static final Map<String, String> TEMPORARY_IMAGE = Map.of(
      "userProfileImageId", "temp-img-001",
      "userId", "user_with_profile_image",
      "storedFileName", "temp_1234abcd.webp",
      "imageUrl", "/images/temp_1234abcd.webp",
      "followStatusType", "TEMPORARY"
  );

  /*CONFIRMED 이미지 데이터*/
  public static final Map<String, String> CONFIRMED_IMAGE = Map.of(
      "userProfileImageId", "confirmed-img-001",
      "userId", "user_with_profile_image",
      "storedFileName", "confirmed_abcd5678.webp",
      "imageUrl", "/images/confirmed_abcd5678.webp",
      "followStatusType", "CONFIRMED"
  );

  /*DELETED 이미지 데이터*/
  public static final Map<String, String> DELETED_IMAGE = Map.of(
      "userProfileImageId", "deleted-img-001",
      "userId", "user_with_profile_image",
      "storedFileName", "deleted_ijkl9012.webp",
      "imageUrl", "/images/deleted_ijkl9012.webp",
      "followStatusType", "DELETED"
  );

  /**
   * 내 프로필 정보 상세 조회 요청
   *
   * @param accessToken
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<GetMyProfileDetailsApiResponse> sendGetMyProfileDetailsRequest(
      String accessToken,
      ResultMatcher expectedStatus) throws Exception {
    return sendGetRequest(
        accessToken, "/api/me/profile", expectedStatus,
        new TypeReference<CommonResponse<GetMyProfileDetailsApiResponse>>() {
        });
  }

  /**
   * 사용자 프로필 초기 설정 요청
   *
   * @return
   */
  public CommonResponse<RegisterMyProfileApiResponse> sendSetMyProfileRequest(
      String accessToken,
      Map<String, String> profileData,
      ResultMatcher conflict) throws Exception {

    String requestBody = generateRequestBody(
        new RegisterUserProfileRequest(
            profileData.get("nickname"),
            profileData.get("statusMessage"),
            profileData.get("bio"),
            profileData.get("phone"),
            UserGenderType.valueOf(profileData.get("gender")),
            profileData.get("profileImageUr")
        )
    );
    return
        sendPostRequest(
            requestBody,
            "/api/me/profile",
            conflict,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 사용자 프로필 업데이트 요청
   *
   * @param accessToken
   * @param newProfileData
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendUpdateMyProfileRequest(String accessToken,
      Map<String, String> newProfileData, ResultMatcher expectedStatus)
      throws Exception {
    String requestBody = generateRequestBody(
        new UpdateMyProfileRequest(
            newProfileData.get("nickname"),
            newProfileData.get("statusMessage"),
            newProfileData.get("bio"),
            newProfileData.get("phone"),
            newProfileData.get("profileImageId")
        )
    );
    return sendPatchRequest(
        requestBody,
        "/api/me/profile",
        expectedStatus,
        new TypeReference<>() {
        },
        Map.of("Authorization", "Bearer " + accessToken)
    );
  }


  /**
   * 사용자 프로필 정보 조회 요청
   *
   * @param accessToken
   * @param userId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<GetUserProfileApiResponse> sendGetUserProfileRequest(String accessToken,
      String userId, ResultMatcher expectedStatus) throws Exception {

    return
        sendGetRequest(
            accessToken, "/api/users/profile/" + userId, expectedStatus,
            new TypeReference<CommonResponse<GetUserProfileApiResponse>>() {
            });
  }

  /**
   * 닉네임 중복 검증 요청
   *
   * @param accessToken
   * @param nickname
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendCheckNicknameRequest(String accessToken, String nickname,
      ResultMatcher expectedStatus) throws Exception {
    return sendGetRequest(accessToken, "/api/me/profile/check?nickname=" + nickname,
        expectedStatus);
  }

  /**
   * 사용자 프로필 응답 검증
   */
  public void assertUserProfileResponse(GetUserProfileApiResponse actual,
      Map<String, String> expected) {
    assertThat(actual.user().userId()).isEqualTo(expected.get("userId"));
    assertThat(actual.user().nickname()).isEqualTo(expected.get("nickname"));
    assertThat(actual.statusMessage()).isEqualTo(expected.get("statusMessage"));
    assertThat(actual.bio()).isEqualTo(expected.get("bio"));
    assertThat(Objects.equals(actual.user().profileImageUrl(), expected.get("profileImageId"))).isTrue();
  }

  /**
   * 사용자 프로필 응답 검증
   */
  public void validateMyProfileDetailsResponse(GetMyProfileDetailsApiResponse actual,
      Map<String, String> expected) {
    assertThat(actual.userId()).isEqualTo(expected.get("userId"));
    assertThat(actual.nickname()).isEqualTo(expected.get("nickname"));
    assertThat(actual.statusMessage()).isEqualTo(expected.get("statusMessage"));
    assertThat(actual.bio()).isEqualTo(expected.get("bio"));
    assertThat(actual.profileImageId()).isEqualTo(expected.get("profileImageId"));
    assertThat(actual.phone()).isEqualTo(expected.get("phone"));

  }

  /**
   * 사용자 프로필 응답 검증
   */
  public void validateMyProfileDetailsResponse(GetMyProfileDetailsApiResponse actual,
      String actualUserId,
      Map<String, String> expected) {
    assertThat(actual.userId()).isEqualTo(actualUserId);
    assertThat(actual.nickname()).isEqualTo(expected.get("nickname"));
    assertThat(actual.statusMessage()).isEqualTo(expected.get("statusMessage"));
    assertThat(actual.bio()).isEqualTo(expected.get("bio"));
    assertThat(actual.profileImageId()).isEqualTo(expected.get("profileImageId"));
    assertThat(actual.phone()).isEqualTo(expected.get("phone"));
  }

  /**
   * 새로운 프로필  데이터 생성
   *
   * @param newNickname
   * @param newStatusMessage
   * @param newBio
   * @param newPhoneNumber
   * @param profileImageId
   * @return
   */
  public Map<String, String> generateNewProfileData(String newNickname, String newStatusMessage,
      String newBio, String newPhoneNumber, String profileImageId) {
    Map<String, String> newProfileData = new HashMap<>();
    newProfileData.put("nickname", newNickname);
    newProfileData.put("statusMessage", newStatusMessage);
    newProfileData.put("bio", newBio);
    newProfileData.put("phone", newPhoneNumber);
    newProfileData.put("profileImageId", profileImageId);

    return newProfileData;
  }

  /**
   * 주어진 파라미터로 내 프로필 조회 요청 후 응답 검증
   *
   * @param accessToken
   * @param actualUserId
   * @param newProfileData
   */
  public void validateMyProfileDetailsResponseAfterUpdate(String accessToken, String actualUserId,
      Map<String, String> newProfileData) throws Exception {
    /*프로필 조회*/
    CommonResponse<GetMyProfileDetailsApiResponse> getMyProfileDetailsResponse = sendGetMyProfileDetailsRequest(
        accessToken, status().isOk());

    //then
    validateMyProfileDetailsResponse(getMyProfileDetailsResponse.getData(),
        actualUserId, newProfileData);
  }

  /**
   * 주어진 파라미터로 프로필 업데이트 요청 후 실패 응답 검증
   * @param accessToken
   * @param newProfileData
   * @param expectedErrorCode
   * @param expectedStatus
   * @throws Exception
   */
  public void validateUpdateMyProfileFailureResponse(String accessToken,
      Map<String, String> newProfileData, ErrorCode expectedErrorCode, ResultMatcher expectedStatus) throws Exception {
    CommonResponse<Void> updateMyProfileResponse = sendUpdateMyProfileRequest(accessToken,
        newProfileData, expectedStatus);

    assertThat(updateMyProfileResponse.isSuccess()).isFalse();
    assertThat(updateMyProfileResponse.getCode()).isEqualTo(
        expectedErrorCode.getCode());
  }
}
