package com.threadly.user.controller.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.user.FollowStatusType;
import com.threadly.user.UserStatusType;
import com.threadly.user.follow.FollowUserApiResponse;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse;
import com.threadly.utils.TestConstants;
import java.time.LocalDateTime;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 팔로우 요청 목록 조회 API 테스트
 */
@DisplayName("팔로우 요청 목록 조회 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetFollowRequestsApiTest extends BaseFollowApiTest {

  /*팔로우 당하는 사용자 id*/
  private static final String TARGET_USER_ID = "private_user";

  /*팔로우 당하는 email*/
  private static final String TARGET_USER_EMAIL = "follow_test_user@threadly.com";

  /*전체 팔로우 요청 수 */
  private static final int FOLLOW_REQUESTS_SIZE = 100;

  /*
   * 1. 팔로우 요청이 없을 경우 빈 응답 검증
   * 2. 팔로우 요청 목록 커서 기반 전체 조회 검증
   * 3. 비공개 계정에 팔로우 요청 후 팔로우 요청 목록 조회 응댭 검증
   * 4. 공개 계정에 팔로우 요청 후 팔로우 요청 목록 조회 응댭 검증
   * 5. 팔로우 요청 목록
   * 6. 팔로우 요청 수락 후 팔로우 요청 목록에서 삭제되는지 검증
   * */

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 팔로우 요청이 없을 경우 팔로우 요청 목록 조회 시 응답 검증*/
    @Order(1)
    @DisplayName("1. 팔로우 요청이 없는 경우 팔로우 요청 목록 조회 시 응답 검증")
    @Test
    public void getFollowRequests_shouldSuccess_01() throws Exception {
      //given

      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      LocalDateTime cursorFollowRequestedAt = null;
      String cursorFollowId = null;
      int limit = 10;

      CommonResponse<GetFollowRequestsApiResponse> getFollowRequestsResponse = sendGetFollowRequestsRequest(
          accessToken, cursorFollowRequestedAt, cursorFollowId, limit, status().isOk());

      //then
      assertThat(getFollowRequestsResponse.getData().followRequests()).isEmpty();
      assertThat(getFollowRequestsResponse.getData().nextCursor().cursorFollowId()).isNull();
      assertThat(
          getFollowRequestsResponse.getData().nextCursor().cursorFollowRequestedAt()).isNull();
    }

    /*[Case #2] 팔로우 요청 목록 전체 조회 검증*/
    @Order(2)
    @DisplayName("2. 팔로우 요청 목록 전체 조회 검증")
    @Test
    public void getFollowRequests_shouldSuccess_02() throws Exception {
      //given
      /*사용자 데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/follow-requests/users.json",
          "/users/follow/follow-requests/user-follows.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TARGET_USER_EMAIL);

      //when
      LocalDateTime cursorFollowRequestedAt = null;
      String cursorFollowId = null;
      int limit = 10;
      int responseDataSize = 0;

      while (true) {
        CommonResponse<GetFollowRequestsApiResponse> getFollowRequestsResponse = sendGetFollowRequestsRequest(
            accessToken, cursorFollowRequestedAt, cursorFollowId, limit, status().isOk()
        );
        responseDataSize += getFollowRequestsResponse.getData().followRequests().size();

        /*마지막 페이지 인 경우*/
        if (getFollowRequestsResponse.getData().nextCursor().cursorFollowId() == null) {
          break;
        }
        cursorFollowId = getFollowRequestsResponse.getData().nextCursor().cursorFollowId();
        cursorFollowRequestedAt = getFollowRequestsResponse.getData().nextCursor()
            .cursorFollowRequestedAt();
      }

      //then
      assertThat(responseDataSize).isEqualTo(FOLLOW_REQUESTS_SIZE);
    }

    /*[Case #3] 비공개 계정에 팔로우 요청 시 팔로우 요청 목록에 추가 되는지 검증*/
    @Order(3)
    @DisplayName("3. 비공개 계정에 팔로우 요청 시 팔로우 요청 목록에 추가 되는지 검증")
    @Test
    public void getFollowRequests_shouldSuccess_03() throws Exception {
      //given
      /*사용자 데이터 삽입*/
      userFixtureLoader.load("/users/profile/user.json");
      userFixtureLoader.load("/users/profile/user2.json", UserStatusType.ACTIVE, true);

      /*로그인*/
      String requesterAccessToken = getAccessToken(USER_EMAIL);

      /*팔로우 요청*/
      CommonResponse<FollowUserApiResponse> followRequestResponse = sendFollowUserRequest(
          requesterAccessToken, USER2_ID, status().isOk());

      //when
      /*로그인 후 팔로우 요청 목록 조회*/
      String targetUserAccessToken = getAccessToken(USER2_EMAIL);
      CommonResponse<GetFollowRequestsApiResponse> getFollowRequestsResponse = sendGetFollowRequestsRequest(
          targetUserAccessToken, null, null, 10, status().isOk());

      //then
      /*검증*/
      assertThat(followRequestResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.PENDING);

      assertThat(getFollowRequestsResponse.getData().followRequests().getFirst().requester()
          .userId()).isEqualTo(
          USER_ID);
    }

    /*[Case #4] 공개 팔로우 요청 시 팔로우 요청 목록에 추가 되지 않는지 검증*/
    @Order(4)
    @DisplayName("4. 공개 계정에 팔로우 요청 시 팔로우 요청 목록에 추가 되지 않는지 검증")
    @Test
    public void getFollowRequests_shouldSuccess_04() throws Exception {
      //given
      /*사용자 데이터 삽입*/
      userFixtureLoader.load("/users/profile/user.json");
      userFixtureLoader.load("/users/profile/user2.json", UserStatusType.ACTIVE, false);

      /*로그인*/
      String requesterAccessToken = getAccessToken(USER_EMAIL);

      /*팔로우 요청*/
      CommonResponse<FollowUserApiResponse> followRequestResponse = sendFollowUserRequest(
          requesterAccessToken, USER2_ID, status().isOk());

      //when
      /*로그인 후 팔로우 요청 목록 조회*/
      String targetUserAccessToken = getAccessToken(USER2_EMAIL);
      CommonResponse<GetFollowRequestsApiResponse> getFollowRequestsResponse = sendGetFollowRequestsRequest(
          targetUserAccessToken, null, null, 10, status().isOk());

      //then
      /*검증*/
      assertThat(followRequestResponse.getData().followStatusType()).isEqualTo(
          FollowStatusType.APPROVED);
      assertThat(getFollowRequestsResponse.getData().followRequests()).isEmpty();
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {
    /*[Case #1] 존재하지 않는 */

  }


}
