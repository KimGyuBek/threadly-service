package com.threadly.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.follow.in.query.dto.FollowRequestResponse;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 팔로우 요청 취소 API 테스트
 */
@DisplayName("팔로우 요청 취소 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class CancelFollowRequestApiTest extends BaseFollowApiTest {

  // usr1 (follower of approve_follow)
  public static final String APPROVE_FOLLOW_FOLLOWER_ID = "usr1";
  public static final String APPROVE_FOLLOW_FOLLOWER_EMAIL = "sunset_gazer1@threadly.com";

  // usr2 (follower of pending_follow)
  public static final String PENDING_FOLLOW_FOLLOWER_ID = "usr2";
  public static final String PENDING_FOLLOW_FOLLOWER_EMAIL = "sky_gazer2@threadly.com";

  // usr3 (공통 following 대상)
  public static final String FOLLOWING_USER_ID = "usr3";
  public static final String FOLLOWING_USER_EMAIL = "book_worm3@threadly.com";

  /*
   * 1. 정상적인 팔로우 요청 취소인 경우 성공
   * 2. PENDING 상태가 아닌 팔로우를 취소 요청 할 경우
   * 3. 존재하지 않는 팔로우 관계에 대해 취소 요청
   * 4. 이미 취소 처리된 요청에 대해서 요청 시
   * */

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 정상적인 팔로우 요청 취소인 경우 검증*/
    @DisplayName("1. 정상적인 팔로우 요청 취소인 경우 검증")
    @Test
    public void cancelFollowRequest_shouldSuccess_01() throws Exception {
      //given
      /*데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/cancel-follow-request/users.json",
          "/users/follow/cancel-follow-request/follows.json"
      );

      /*팔로우 요청 목록 조회*/
      CommonResponse<CursorPageApiResponse<FollowRequestResponse>> getFollowRequestsResponse1 = sendGetFollowRequestsRequest(
          getAccessToken(FOLLOWING_USER_EMAIL), null, null, 10,
          status().isOk());

      //when
      /*팔로우 요청 취소 */
      sendCancelFollowRequest(
          getAccessToken(PENDING_FOLLOW_FOLLOWER_EMAIL), FOLLOWING_USER_ID, status().isOk()
      );

      /*팔로우 요청 목록 조회*/
      CommonResponse<CursorPageApiResponse<FollowRequestResponse>> getFollowRequestsResponse2 = sendGetFollowRequestsRequest(
          getAccessToken(FOLLOWING_USER_EMAIL), null, null, 10,
          status().isOk());
      //then

      assertThat(getFollowRequestsResponse1.getData().content().size()).isEqualTo(
          getFollowRequestsResponse2.getData().content().size() + 1);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1]  PENDING 상태가 아닌 팔로우를 취소 요청 할 경우 실패 검증*/
    @Order(1)
    @DisplayName("1. PENDING 상태가 아닌 팔로우를 취소 요청 할 경우 실패 검증")
    @Test
    public void cancelFollowRequest_shouldFaile_01() throws Exception {
      //given
      /*데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/cancel-follow-request/users.json",
          "/users/follow/cancel-follow-request/follows.json"
      );

      //when
      /*팔로우 요청 취소 */
      CommonResponse<Void> cancelFollowRequestResponse = sendCancelFollowRequest(
          getAccessToken(APPROVE_FOLLOW_FOLLOWER_EMAIL), FOLLOWING_USER_ID, status().isNotFound()
      );

      //then
      validateFailResponse(cancelFollowRequestResponse, ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }

    /*[Case #2]  존재하지 않는 팔로우 관계에 대해서 요청 취소를 할 경우 실패 검증*/
    @Order(2)
    @DisplayName("2, 존재하지 않는 팔로우 관계에 대해서 요청 취로를 할 경우 실패 검증")
    @Test
    public void cancelFollowRequest_shouldFaile_02() throws Exception {
      //given
      //when
      /*팔로우 요청 취소 */
      CommonResponse<Void> cancelFollowRequestResponse = sendCancelFollowRequest(
          getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1), USER_ID, status().isNotFound()
      );

      //then
      validateFailResponse(cancelFollowRequestResponse, ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }

    /*[Case #3] 이미 취소 처리된 팔로우 요청에 대해서 취소 요청 할 경우 실패 검증*/
    @Order(3)
    @DisplayName("3. 이미 취소 처리된 팔로우 요청에 대해서 취소 요청을 할 경우 실패 검증")
    @Test
    public void cancelFollowRequest_shouldFail_03() throws Exception {
      //given
      /*데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/cancel-follow-request/users.json",
          "/users/follow/cancel-follow-request/follows.json"
      );

      //when
      /*팔로우 요청 취소 */
      sendCancelFollowRequest(
          getAccessToken(PENDING_FOLLOW_FOLLOWER_EMAIL), FOLLOWING_USER_ID, status().isOk()
      );
      /*팔로우 요청 취소 */
      CommonResponse<Void> cancelFollowRequestResponse = sendCancelFollowRequest(
          getAccessToken(PENDING_FOLLOW_FOLLOWER_EMAIL), FOLLOWING_USER_ID, status().isNotFound()
      );

      //then
      validateFailResponse(cancelFollowRequestResponse, ErrorCode.FOLLOW_RELATION_NOT_FOUND);

    }
  }
}


