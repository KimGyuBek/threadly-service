package com.threadly.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.core.usecase.follow.command.dto.FollowUserApiResponse;
import com.threadly.core.usecase.follow.query.dto.FollowRequestResponse;
import com.threadly.core.usecase.follow.query.dto.FollowerResponse;
import com.threadly.core.usecase.follow.query.dto.FollowingApiResponse;
import com.threadly.core.usecase.follow.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.follow.request.FollowRequest;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.testsupport.fixture.users.UserFollowFixtureLoader;
import com.threadly.user.controller.profile.BaseUserProfileApiTest;
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
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<CursorPageApiResponse<FollowerResponse>> sendGetFollowersRequest(
      String accessToken, String targetUserId, LocalDateTime cursorTimestamp,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/followers?limit=" + limit;
    if (targetUserId != null) {
      path += "&user_id=" + targetUserId;
    }

    if (cursorTimestamp != null || cursorId != null) {
      path += "&cursor_timestamp=" + cursorTimestamp + "&cursor_id="
          + cursorId;
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
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<CursorPageApiResponse<FollowingApiResponse>> sendGetFollowingsRequest(
      String accessToken, String targetUserId, LocalDateTime cursorTimestamp,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/followings?limit=" + limit;
    if (targetUserId != null) {
      path += "&user_id=" + targetUserId;
    }

    if (cursorTimestamp != null || cursorId != null) {
      path += "&cursor_timestamp=" + cursorTimestamp + "&cursor_id="
          + cursorId;
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
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @param expectedStatus
   * @return
   */
  public CommonResponse<CursorPageApiResponse<FollowRequestResponse>> sendGetFollowRequestsRequest(
      String accessToken, LocalDateTime cursorTimestamp, String cursorId, int limit,
      ResultMatcher expectedStatus)
      throws Exception {
    String path = "/api/follows/requests?limit=" + limit;

    if (cursorTimestamp != null || cursorId != null) {
      path += "&cursor_timestamp=" + cursorTimestamp + "&cursor_id="
          + cursorId;
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
   * 팔로우 요청 수락 요청
   *
   * @param accessToken
   * @param followId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendApproveFollowRequest(String accessToken, String followId,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendPatchRequest(
            "",
            "/api/follows/" + followId + "/approve",
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 팔로우 요청 거절 요청
   *
   * @param accessToken
   * @param followId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendRejectFollowRequest(String accessToken, String followId,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendDeleteRequest(
            "",
            "/api/follows/" + followId,
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 팔로우 요청 취소 요청
   *
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendCancelFollowRequest(String accessToken, String targetUserId,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendDeleteRequest(
            "", "/api/follows/requests/" + targetUserId,
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 주어진 targetUserId에 해당하는 사용자 언팔로우 요청
   *
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendUnfollowUserRequest(String accessToken, String targetUserId,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendDeleteRequest(
            "", "/api/follows/following/" + targetUserId,
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 주어진 targetUserId에 해당하는 사용자 팔로워 삭제 요청
   *
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendRemoveFollowerRequest(String accessToken, String targetUserId,
      ResultMatcher expectedStatus)
      throws Exception {
    return
        sendDeleteRequest(
            "", "/api/follows/followers/" + targetUserId,
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로워, 팔로잉 수 조회 요청
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetUserFollowStatsApiResponse> sendGetUserFollowStatsRequest(String accessToken,
      String targetUserId, ResultMatcher expectedStatus) throws Exception {
    return
        sendGetRequest(
            accessToken, "/api/follows/" + targetUserId + "/stats",
            expectedStatus,
            new TypeReference<>() {
            }
        );
  }

  /**
   * 주어진 followerId가 팔로워 목록에 포함되는지 검증
   *
   * @param accessToken
   * @param followerId
   * @throws Exception
   */
  public void assertFollowerExists(String accessToken, String followerId) throws Exception {
    CommonResponse<CursorPageApiResponse<FollowerResponse>> getFollowersResponse = sendGetFollowersRequest(
        accessToken, null, null, null, 10, status().isOk());
    assertThat(
        getFollowersResponse.getData().content().getFirst().follower().userId()).isEqualTo(
        followerId);
  }

  /**
   * 주어진 followerId가 팔로워 목록에 포함되지 않는지 검증
   *
   * @param accessToken
   * @throws Exception
   */
  public void assertFollowersIsEmpty(String accessToken) throws Exception {
    CommonResponse<CursorPageApiResponse<FollowerResponse>> getFollowersResponse = sendGetFollowersRequest(
        accessToken, null, null, null, 10, status().isOk());
    assertThat(getFollowersResponse.getData().content()).isEmpty();
  }

}
