package com.threadly.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.core.port.follow.in.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("사용자 팔로워, 팔로잉 수 조회 API 테스트")
public class GetFollowStatsApiTest extends BaseFollowApiTest {

  // 팔로우 통계 테스트용 메인 사용자
  public static final String MAIN_USER_ID = "main_user";
  public static final String MAIN_USER_EMAIL = "main@threadly.com";

  // 팔로우 통계 기대값
  public static final int EXPECTED_FOLLOWER_COUNT = 10;
  public static final int EXPECTED_FOLLOWING_COUNT = 20;

  // 팔로워 사용자 이메일
  public static final String FOLLOWER_01_EMAIL = "follower01@threadly.com";

  public static final String FOLLOWING_01_EMAIL = "following01@threadly.com";

  /*
   * 1. 자신에 대해서 요청 시 응답 검증
   * 2. 다른 사용자에 대해서 요청 시 응답 검증
   * 3. 팔로워 및 팔로잉 사용자 탈퇴 처리 후 조회 시 수가 감소되는지 검증
   * 4. 팔로워 및 팔로잉 사용자 비활성화 후 조회 시 수가 감소되는지 검증
   * 5. PENDING 상태의 팔로우가 포함되지 않는지 검증
   * 6. 존재하지 않는 사용자에 대한 팔로워, 팔로잉 수  조회 요청
   * 7. 탈퇴 처리된 사용자에 대한 팔로워, 팔로잉 수 조회 요청
   * 8. 비활성화 처리된 사용자에 대한 팔로워, 팔로잉 수 조회 요청
   * */


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 자신의 팔로워, 팔로잉 수 조회 요청 시 응답 검증*/
    @Order(1)
    @DisplayName("1. 자신의 팔로워, 팔로잉 수 조회 요청 응답 검증")
    @Test
    public void getUserFollowStats_shouldSuccess_01() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load("/users/follow/follow-stats/main-user.json");
      userFollowFixtureLoader.load(
          "/users/follow/follow-stats/users.json",
          "/users/follow/follow-stats/follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(MAIN_USER_EMAIL);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      //then
      assertThat(getUserFollowStatsResponse.getData().followerCount()).isEqualTo(
          EXPECTED_FOLLOWER_COUNT);
      assertThat(getUserFollowStatsResponse.getData().followingCount()).isEqualTo(
          EXPECTED_FOLLOWING_COUNT);
    }

    /*[Case #2] 다른 사용자의 팔로워, 팔로잉 수 조회 요청 시 응답 검증*/
    @Order(2)
    @DisplayName("2. 정상적인 팔로워, 팔로잉 수 조회 요청 응답 검증")
    @Test
    public void getUserFollowStats_shouldSuccess_02() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load("/users/follow/follow-stats/main-user.json");
      userFollowFixtureLoader.load(
          "/users/follow/follow-stats/users.json",
          "/users/follow/follow-stats/follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      //then
      assertThat(getUserFollowStatsResponse.getData().followerCount()).isEqualTo(
          EXPECTED_FOLLOWER_COUNT);
      assertThat(getUserFollowStatsResponse.getData().followingCount()).isEqualTo(
          EXPECTED_FOLLOWING_COUNT);
    }

    /*[Case #3] 팔로워 및 팔로잉 사용자 탈퇴 후 조회 시 각각 수가 감소되는지 검증*/
    @Order(3)
    @DisplayName("3. 팔로워 및 팔로잉 사용자 탈퇴 후 조회 시 각각 수가 감소되는지 검증")
    @Test
    public void getUserFollowStats_shouldSuccess_03() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load("/users/follow/follow-stats/main-user.json");
      userFollowFixtureLoader.load(
          "/users/follow/follow-stats/users.json",
          "/users/follow/follow-stats/follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse1 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      /*팔로워 1명 탈퇴 처리*/
      String followerAccessToken = getAccessToken(FOLLOWER_01_EMAIL);
      sendWithdrawMyAccountRequest(followerAccessToken, getXVerifyToken(followerAccessToken),
          status().isOk());

      /*팔로잉 사용자 1명 탈퇴 처리*/
      String followingUserAccessToken = getAccessToken(FOLLOWING_01_EMAIL);
      sendWithdrawMyAccountRequest(followingUserAccessToken,
          getXVerifyToken(followingUserAccessToken), status().isOk());

      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse2 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      //then
      assertThat(getUserFollowStatsResponse1.getData().followerCount()).isEqualTo(
          getUserFollowStatsResponse2.getData().followerCount() + 1);
      assertThat(getUserFollowStatsResponse1.getData().followingCount()).isEqualTo(
          getUserFollowStatsResponse2.getData().followingCount() + 1);
    }

    /*[Case #4] 팔로워 및 팔로잉 사용자 비활성화 후 조회 시 각각 수가 감소되는지 검증*/
    @Order(4)
    @DisplayName("4. 팔로워 및 팔로잉 사용자 비활성화 후 조회 시 각각 수가 감소되는지 검증")
    @Test
    public void getUserFollowStats_shouldSuccess_04() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load("/users/follow/follow-stats/main-user.json");
      userFollowFixtureLoader.load(
          "/users/follow/follow-stats/users.json",
          "/users/follow/follow-stats/follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse1 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      /*팔로워 1명 비활성화 처리*/
      String followerAccessToken = getAccessToken(FOLLOWER_01_EMAIL);
      sendDeactivateMyAccountRequest(followerAccessToken, getXVerifyToken(followerAccessToken),
          status().isOk());

      /*팔로잉 사용자 1명 비활성화 처리*/
      String followingUserAccessToken = getAccessToken(FOLLOWING_01_EMAIL);
      sendDeactivateMyAccountRequest(followingUserAccessToken,
          getXVerifyToken(followingUserAccessToken), status().isOk());

      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse2 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      //then
      assertThat(getUserFollowStatsResponse1.getData().followerCount()).isEqualTo(
          getUserFollowStatsResponse2.getData().followerCount() + 1);
      assertThat(getUserFollowStatsResponse1.getData().followingCount()).isEqualTo(
          getUserFollowStatsResponse2.getData().followingCount() + 1);
    }

    /*[Case #5] PENDING 상태의 팔로우가 포함 되지 않는지 검증*/
    @Order(5)
    @DisplayName("5. PENDING 상태의 팔로우가 포함 되지 않는지 검증")
    @Test
    public void getUserFollowStats_shouldSuccess_05() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load("/users/follow/follow-stats/main-user.json", UserStatusType.ACTIVE,
          true);
      userFollowFixtureLoader.load(
          "/users/follow/follow-stats/users.json",
          "/users/follow/follow-stats/follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse1 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      /*팔로우 요청*/
      sendFollowUserRequest(accessToken, MAIN_USER_ID, status().isOk());

      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse2 = sendGetUserFollowStatsRequest(
          accessToken, MAIN_USER_ID, status().isOk()
      );

      //then
      assertThat(getUserFollowStatsResponse2.getData().followerCount()).isEqualTo(
          EXPECTED_FOLLOWER_COUNT);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 존재하지 않는 사용자에 대한 요청 응답 검증*/
    @Order(1)
    @DisplayName("1. 존재하지 않는 사용자에 대한 응답 검증")
    @Test
    public void getUserFollowStats_shouldFail_01() throws Exception {
      //given

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse = sendGetUserFollowStatsRequest(
          accessToken, "not_exists_user", status().isNotFound()
      );

      //then
      validateFailResponse(getUserFollowStatsResponse, ErrorCode.USER_NOT_FOUND);
    }

    /*[Case #2] 탈퇴 처리된 사용자에 대한 요청 응답 검증*/
    @Order(2)
    @DisplayName("2. 탈퇴 처리된 사용자에 대한 요청 응답 검증")
    @Test
    public void getUserFollowStats_shouldFail_02() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load(
          "/users/profile/user2.json", UserStatusType.DELETED
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse = sendGetUserFollowStatsRequest(
          accessToken, USER2_ID, status().isForbidden()
      );

      //then
      validateFailResponse(getUserFollowStatsResponse, ErrorCode.USER_ALREADY_DELETED);
    }

    /*[Case #3] 비활성화 처리된 사용자에 대한 요청 응답 검증*/
    @Order(3)
    @DisplayName("3. 비활성화 처리된 사용자에 대한 요청 응답 검증")
    @Test
    public void getUserFollowStats_shouldFail_03() throws Exception {
      //given
      /*데이터 삽입*/
      userFixtureLoader.load(
          "/users/profile/user2.json", UserStatusType.INACTIVE
      );

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*조회 요청*/
      CommonResponse<GetUserFollowStatsApiResponse> getUserFollowStatsResponse = sendGetUserFollowStatsRequest(
          accessToken, USER2_ID, status().isForbidden()
      );

      //then
      validateFailResponse(getUserFollowStatsResponse, ErrorCode.USER_INACTIVE);
    }
  }


}
