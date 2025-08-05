package com.threadly.user.controller.follow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.testsupport.fixture.users.UserFollowFixtureLoader;
import com.threadly.user.controller.profile.BaseUserProfileApiTest;
import com.threadly.user.follow.FollowUserApiResponse;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse;
import com.threadly.user.follow.get.GetFollowersApiResponse;
import com.threadly.user.follow.get.GetFollowingsApiResponse;
import com.threadly.user.request.follow.FollowRequest;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Follow 관련 Base 테스트 클래스
 */
public abstract class BaseFollowApiTest extends BaseUserProfileApiTest {

  @Autowired
  public UserFollowFixtureLoader userFollowFixtureLoader;

  /**
   * follow 요청 전송
   *
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<FollowUserApiResponse> sendFollowUserRequest(String accessToken,
      String targetUserId, ResultMatcher expectedStatus) throws Exception {
    String requestBody = generateRequestBody(
        new FollowRequest(targetUserId)
    );
    return
        sendPostRequest(
            requestBody,
            "/api/follows",
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 팔로워 목록 조회 요청
   *
   * @param accessToken
   * @param cursorFollowedAt
   * @param cursorFollowerId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetFollowersApiResponse> sendGetFollowersRequest(
      String accessToken, String targetUserId, LocalDateTime cursorFollowedAt,
      String cursorFollowerId,
      int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/followers?limit=" + limit;
    if (targetUserId != null) {
      path += "&user_id=" + targetUserId;
    }

    if (cursorFollowedAt != null || cursorFollowerId != null) {
      path += "&cursor_followed_at=" + cursorFollowedAt + "&cursor_follower_id="
          + cursorFollowerId;
    }
    return
        sendGetRequest(
            accessToken,
            path,
            expectedStatus,
            new TypeReference<>() {
            }
        );
  }

  /**
   * 팔로잉 목록 조회 요청
   *
   * @param accessToken
   * @param cursorFollowedAt
   * @param cursorFollowingId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetFollowingsApiResponse> sendGetFollowingsRequest(
      String accessToken, String targetUserId, LocalDateTime cursorFollowedAt,
      String cursorFollowingId,
      int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/followings?limit=" + limit;
    if (targetUserId != null) {
      path += "&user_id=" + targetUserId;
    }

    if (cursorFollowedAt != null || cursorFollowingId != null) {
      path += "&cursor_followed_at=" + cursorFollowedAt + "&cursor_following_id="
          + cursorFollowingId;
    }
    return
        sendGetRequest(
            accessToken,
            path,
            expectedStatus,
            new TypeReference<>() {
            }
        );
  }

  /**
   * 팔로우 요청 목록 조회 요청
   *
   * @param accessToken
   * @param cursorFollowRequestedAt
   * @param cursorFollowId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetFollowRequestsApiResponse> sendGetFollowRequestsRequest(
      String accessToken, LocalDateTime cursorFollowRequestedAt, String cursorFollowId, int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/requests?limit=" + limit;

    if (cursorFollowRequestedAt != null || cursorFollowId != null) {
      path += "&cursor_follow_requested_at=" + cursorFollowRequestedAt + "&cursor_follow_id="
          + cursorFollowId;
    }
    return
        sendGetRequest(
            accessToken,
            path,
            expectedStatus,
            new TypeReference<>() {
            }
        );
  }

}
