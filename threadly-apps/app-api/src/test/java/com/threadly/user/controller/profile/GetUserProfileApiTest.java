package com.threadly.user.controller.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.follow.BaseFollowApiTest;
import com.threadly.core.domain.follow.FollowStatusType;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.usecase.user.profile.query.dto.GetUserProfileApiResponse;
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

/**
 * 사용자 프로필 조회 관련 테스트
 */
@DisplayName("사용자 프로필 조회 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class GetUserProfileApiTest extends BaseFollowApiTest {

  // 테스트용 상수들
  private static final String PUBLIC_USER_1_ID = "public_user_1";
  private static final String PUBLIC_USER_2_ID = "public_user_2";
  private static final String PRIVATE_USER_1_ID = "private_user_1";
  private static final String PRIVATE_USER_2_ID = "private_user_2";
  private static final String PRIVATE_USER_3_ID = "private_user_3";

  @BeforeEach
  void setUp() {
    userFixtureLoader.load(
        "/users/profile/user.json", UserStatusType.ACTIVE
    );
  }

  /*
   * 프로필 조회 테스트 시나리오:
   * 
   * [성공 케이스] - 모든 경우에 기본 프로필 정보는 조회 가능 (200 OK)
   * 1. 공개 계정 - 팔로우 관계 없음 (NONE 상태)
   * 2. 공개 계정 - 팔로우 승인됨 (APPROVED 상태) 
   * 3. 비공개 계정 - 팔로우 승인됨 (APPROVED 상태)
   * 4. 본인 프로필 조회 (SELF 상태)
   * 5. 비공개 계정 - 팔로우 관계 없음 (NONE 상태)
   * 6. 비공개 계정 - 팔로우 요청 대기중 (PENDING 상태)
   * 
   * [실패 케이스] - 시스템 오류 또는 권한 문제
   * 7. 존재하지 않는 userId로 조회 (404 Not Found)
   * 8. 프로필을 설정하지 않은 사용자 조회 (404 Not Found) 
   * 9. 탈퇴 처리된 사용자 조회 (403 Forbidden)
   * 10. 비활성화 처리된 사용자 조회 (403 Forbidden)
   * 
   * 참고: 비공개 계정이어도 기본 프로필(닉네임, 상태메시지, 바이오)은 
   *      누구나 조회 가능하며, 단지 FollowStatusType으로 관계 상태만 구분함
   * */

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 사용자의 프로필이 존재하고 공개 계정이면 팔로우 상태가 아니더라도 조회가 가능*/
    @Order(1)
    @DisplayName("1. 사용자 프로필이 존재하고 공개 계정이면 팔로우 상태가 아니더라도 조회가 가능한지 검증")
    @Test
    public void getUserProfile_shouldSuccess_01() throws Exception {
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

    /*[Case #2] 공개 계정 - 팔로우 승인된 상태*/
    @Order(2)
    @DisplayName("2. 공개 계정을 팔로우한 상태에서 APPROVED 상태로 프로필 조회 가능")
    @Test
    public void getUserProfile_shouldSuccess_02() throws Exception {
      //given
      /*공개 계정 사용자 및 팔로우 관계 데이터 로딩*/
      userFixtureLoader.load("/users/profile/get-user-profile/public-users.json");
      userFollowFixtureLoader.load(
          "/users/profile/get-user-profile/private-users.json",
          "/users/profile/get-user-profile/follow-relationships.json"
      );

      /*로그인 - 테스트 기본 사용자*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, PUBLIC_USER_1_ID, status().isOk());

      //then
      /*프로필 데이터 검증*/
      assertThat(getUserProfileResponse.getData().user().userId()).isEqualTo(PUBLIC_USER_1_ID);
      assertThat(getUserProfileResponse.getData().user().nickname()).isEqualTo("public_user_1");
      /*팔로우 상태 검증 - 공개 계정이지만 팔로우 승인 상태*/
      assertThat(getUserProfileResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.APPROVED);
    }

    /*[Case #3] 비공개 계정 - 팔로우 승인된 상태*/
    @Order(3)
    @DisplayName("3. 비공개 계정을 팔로우 승인받은 상태에서 APPROVED 상태로 프로필 조회 가능")
    @Test
    public void getUserProfile_shouldSuccess_03() throws Exception {
      //given
      /*비공개 계정 사용자 및 팔로우 관계 데이터 로딩*/
      userFixtureLoader.load("/users/profile/get-user-profile/public-users.json");
      userFollowFixtureLoader.load(
          "/users/profile/get-user-profile/private-users.json", true,
          "/users/profile/get-user-profile/follow-relationships.json"
      );

      /*로그인 - 테스트 기본 사용자*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, PRIVATE_USER_1_ID, status().isOk());

      //then
      /*프로필 데이터 검증 - 비공개 계정이어도 기본 정보는 조회 가능*/
      assertThat(getUserProfileResponse.getData().user().userId()).isEqualTo(PRIVATE_USER_1_ID);
      assertThat(getUserProfileResponse.getData().user().nickname()).isEqualTo("private_user_1");
      /*팔로우 상태 검증 - 비공개 계정이지만 팔로우 승인 상태*/
      assertThat(getUserProfileResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.APPROVED);
    }

    /*[Case #4] 본인 프로필 조회*/
    @Order(4)
    @DisplayName("4. 본인 프로필 조회 시 SELF 상태로 조회 가능")
    @Test
    public void getUserProfile_shouldSuccess_04() throws Exception {
      //given
      /*로그인 - 본인 계정으로 로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      /*본인 프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER_ID, status().isOk());

      //then
      /*프로필 데이터 검증*/
      assertUserProfileResponse(getUserProfileResponse.getData(), USER_PROFILE);
      /*팔로우 상태 검증 - 본인 프로필은 SELF 상태*/
      assertThat(getUserProfileResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.SELF);
    }

    /*[Case #5] 비공개 계정 - 팔로우 관계 없음*/
    @Order(5)
    @DisplayName("5. 비공개 계정을 팔로우하지 않은 상태에서 NONE 상태로 기본 프로필 조회 가능")
    @Test
    public void getUserProfile_shouldSuccess_05() throws Exception {
      //given
      /*비공개 계정 사용자 데이터 로딩*/
      userFixtureLoader.load(
          "/users/profile/get-user-profile/private-users.json", UserStatusType.ACTIVE, true
      );

      /*로그인 - 팔로우 관계 없는 다른 사용자로 로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청 - 팔로우 관계 없는 비공개 계정*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, PRIVATE_USER_3_ID, status().isOk());

      //then
      /*팔로우 상태 검증 - 비공개 계정이지만 기본 프로필은 조회 가능, NONE 상태 반환*/
      assertThat(getUserProfileResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.NONE);
    }

    /*[Case #6] 비공개 계정 - 팔로우 요청 대기중*/
    @Order(6)
    @DisplayName("6. 비공개 계정에 팔로우 요청 대기중인 상태에서 PENDING 상태로 기본 프로필 조회 가능")
    @Test
    public void getUserProfile_shouldSuccess_06() throws Exception {
      //given
      /*비공개 계정 사용자 및 팔로우 관계 데이터 로딩*/
      userFixtureLoader.load("/users/profile/get-user-profile/public-users.json");
      userFollowFixtureLoader.load(
          "/users/profile/get-user-profile/private-users.json", true,
          "/users/profile/get-user-profile/follow-relationships.json"
      );

      /*로그인 - 팔로우 요청 대기중인 사용자로 로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      /*프로필 조회 요청 - 팔로우 요청 대기 중인 비공개 계정*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, PRIVATE_USER_2_ID, status().isOk());

      //then
      /*팔로우 상태 검증 - 비공개 계정이지만 기본 프로필은 조회 가능, PENDING 상태 반환*/
      assertThat(getUserProfileResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.PENDING);
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
    public void getUserProfile_shouldFail_01() throws Exception {
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
    @DisplayName("2. 사용자는 존재하지만 profile이 없는 경우 404 Not Found")
    @Test
    public void getUserProfile_shouldFail_03() throws Exception {
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

    /*[Case #3]  탈퇴한 사용자를 조회할 경우 */
    @Order(3)
    @DisplayName("3. 탈퇴 처리된 사용자의 프로필을 조회할 경우 403 Forbidden")
    @Test
    public void getUserProfile_shouldFail_04() throws Exception {
      //given
      userFixtureLoader.load(
          "/users/profile/user2.json",
          UserStatusType.DELETED
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER2_ID, status().isForbidden());

      //then
      /*응답 검증*/
      assertThat(getUserProfileResponse.isSuccess()).isFalse();
      assertThat(getUserProfileResponse.getCode()).isEqualTo(
          ErrorCode.USER_ALREADY_DELETED.getCode());
    }

    /*[Case #4]  비활성화 된 사용자를 조회하는 경우 실패 검증 */
    @Order(4)
    @DisplayName("4. 비활성화 된 사용자를 조회하는 경우 403 Forbidden")
    @Test
    public void getUserProfile_shouldFail_05() throws Exception {
      //given
      /*비활성화 된 사용자 데이터 삽입*/
      userFixtureLoader.load(
          "/users/profile/user2.json",
          UserStatusType.INACTIVE
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*프로필 조회 요청*/
      CommonResponse<GetUserProfileApiResponse> getUserProfileResponse = sendGetUserProfileRequest(
          accessToken, USER2_ID, status().isForbidden());

      //then
      /*응답 검증*/
      assertThat(getUserProfileResponse.isSuccess()).isFalse();
      assertThat(getUserProfileResponse.getCode()).isEqualTo(
          ErrorCode.USER_INACTIVE.getCode());
    }
    /*[Case #3]  차단된 사용자를 조회할 경우*/
  }
}