package com.threadly.follow;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 팔로우 요청 수락 및 거절 관련 API 테스트
 */
@DisplayName("팔로우 요청 수락 및 거절 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class HandleFollowRequestApiTest extends BaseFollowApiTest {

  // test-user.json
  public static final String TEST_USER_ID = "test_user";
  public static final String TEST_USER_EMAIL = "test_user@threadly.com";

  // target-user.json
  public static final String TARGET_USER_ID = "target_user";
  public static final String TARGET_USER_EMAIL = "target_user@threadly.com";

  // follow.json
  public static final String TEST_PENDING_FOLLOW_ID = "follow_0";

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("팔로우 요청 수락 테스트")
  @Nested
  class ApproveFollowRequestTest {


    /*
     * 1. 정상적인 팔로우 요청 수락 성공 검증
     * 2. 존재하지 않는 팔로우 요청에 대한 실패 검증
     * 3. 내가 받은 팔로우 요청이 아닌 경우 실패 검증
     * 4. 이미 수락한 팔로우 요청에 대한 실패 검증
     * */

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 정상적인 팔로우 요청 수락 성공 검증*/
      @Order(1)
      @DisplayName("1. 정상적인 팔로우 요청 수락 성공 검증")
      @Test
      public void approveFollowRequest_shouldSuccess_01() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        sendApproveFollowRequest(accessToken, TEST_PENDING_FOLLOW_ID,
            status().isOk());

        //then
        /*팔로워 목록 조회 검증*/
        assertFollowerExists(accessToken, TEST_USER_ID);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 팔로우 요청에 대한 실패 검증*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 팔로우 요청에 대한 실패 검증")
      @Test
      public void approveFollowRequest_shouldFail_01() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        CommonResponse<Void> approveFollowResponse = sendApproveFollowRequest(accessToken,
            "not_exists_follow_id",
            status().isNotFound());

        //then
        validateFailResponse(approveFollowResponse, ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
      }

      /*[Case #2] 내가 받은 팔로우 요청이 아닌 경우 실패 검증*/
      @Order(2)
      @DisplayName("2. 내가 받은 팔로우 요청이 아닌 경우 실패 검증")
      @Test
      public void approveFollowRequest_shouldFail_02() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);
        CommonResponse<Void> approveFollowResponse = sendApproveFollowRequest(accessToken,
            TEST_PENDING_FOLLOW_ID,
            status().isForbidden());

        //then
        validateFailResponse(approveFollowResponse, ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
      }

      /*[Case #3] 이미 수락한 팔로우 요청에 대한 수락 요청 시 실패 검증*/
      @Order(3)
      @DisplayName("3. 이미 수락한 팔로우 요청에 대한 수락 요청 시 실패 검증")
      @Test
      public void approveFollowRequest_shouldFail_03() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        sendApproveFollowRequest(accessToken, TEST_PENDING_FOLLOW_ID,
            status().isOk());

        CommonResponse<Void> approveFollowRequestResponse = sendApproveFollowRequest(accessToken,
            TEST_PENDING_FOLLOW_ID,
            status().isNotFound());

        //then
        validateFailResponse(approveFollowRequestResponse, ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
      }

    }
  }

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("팔로우 요청 거절 테스트")
  @Nested
  class RejectFollowRequestTest {

    /*
     * 1. 정상적인 팔로우 요청 거절 성공 검증
     * 2. 존재하지 않는 팔로우 요청에 대한 실패 검증
     * 3. 내가 받은 팔로우 요청이 아닌 경우 실패 검증
     * 4. 이미 거절한 팔로우 요청에 대한 실패 검증
     * */

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 정상적인 팔로우 요청 거절 성공 검증*/
      @Order(1)
      @DisplayName("1. 정상적인 팔로우 요청 거절 성공 검증")
      @Test
      public void rejectFollowRequest_shouldSuccess_01() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        sendRejectFollowRequest(accessToken, TEST_PENDING_FOLLOW_ID,
            status().isOk());

        //then
        /*팔로워 목록에 포함되지 않는지 검증*/
        assertFollowersIsEmpty(accessToken);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 팔로우 요청에 대한 실패 검증*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 팔로우 요청에 대한 실패 검증")
      @Test
      public void rejectFollowRequest_shouldFail_01() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        CommonResponse<Void> approveFollowResponse = sendRejectFollowRequest(accessToken,
            "not_exists_follow_id",
            status().isNotFound());

        //then
        validateFailResponse(approveFollowResponse, ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
      }

      /*[Case #2] 내가 받은 팔로우 요청이 아닌 경우 실패 검증*/
      @Order(2)
      @DisplayName("2. 내가 받은 팔로우 요청이 아닌 경우 실패 검증")
      @Test
      public void rejectFollowRequest_shouldFail_02() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);
        CommonResponse<Void> approveFollowResponse = sendRejectFollowRequest(accessToken,
            TEST_PENDING_FOLLOW_ID,
            status().isForbidden());

        //then
        validateFailResponse(approveFollowResponse, ErrorCode.FOLLOW_REQUEST_FORBIDDEN);
      }

      /*[Case #3] 이미 거절한 팔로우 요청에 대한 요청 시 실패 검증*/
      @Order(3)
      @DisplayName("3. 이미 거절한 팔로우 요청에 대한 요청 시 실패 검증")
      @Test
      public void rejectFollowRequest_shouldFail_03() throws Exception {
        //given
        /*데이터 삽입*/
        userFixtureLoader.load("/users/follow/handle-request/test-user.json");
        userFollowFixtureLoader.load(
            "/users/follow/handle-request/target-user.json",
            "/users/follow/handle-request/follow.json"
        );

        //when
        /*targetUser 로그인 후 팔로우 수락 검증*/
        String accessToken = getAccessToken(TARGET_USER_EMAIL);
        sendRejectFollowRequest(accessToken, TEST_PENDING_FOLLOW_ID,
            status().isOk());

        CommonResponse<Void> approveFollowRequestResponse = sendRejectFollowRequest(accessToken,
            TEST_PENDING_FOLLOW_ID,
            status().isNotFound());

        //then
        validateFailResponse(approveFollowRequestResponse, ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
      }

    }


  }
}
