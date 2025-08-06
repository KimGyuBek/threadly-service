package com.threadly.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.follow.query.dto.GetFollowingsApiResponse;
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
 * 사용자 언팔로우 API 테스트
 */
@DisplayName("사용자 언팔로우 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class UnfollowApiTest extends BaseFollowApiTest {

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
   * 1. 정상적인 언팔로우 요청 검증
   * 2. 팔로우 관계가 아닌 사용자에 대한 언팔로우 요청 시 실패 검증
   * 3. PENDING 상태의 사용자에 대한 언팔로우 요청 시 실패 검증
   * */

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 정상적인 언팔로우 요청 검증*/
    @Order(1)
    @DisplayName("1. 정상적인 언팔로우 요청 검증")
    @Test
    public void unfollowUser_shouldSuccess_01() throws Exception {
      //given
      /*데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/unfollow/users.json",
          "/users/follow/unfollow/follows.json"
      );

      String accessToken = getAccessToken(APPROVE_FOLLOW_FOLLOWER_EMAIL);

      /*팔로잉 목록 조회*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse1 = sendGetFollowingsRequest(
          accessToken, null, null, null, 10,
          status().isOk()
      );

      //when
      /*언팔로우 요청*/
      sendUnfollowUser(accessToken, FOLLOWING_USER_ID, status().isOk());

      /*팔로잉 목록 조회*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse2 = sendGetFollowingsRequest(
          accessToken, null, null, null, 10,
          status().isOk()
      );

      //then
      assertThat(getFollowingsResponse1.getData().followings().size()).isEqualTo(
          getFollowingsResponse2.getData().followings().size() + 1);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 팔로우 관계가 아닌 사용자에 대한 언팔로우 요청 시 실패 검증*/
    @Order(1)
    @DisplayName("1. 팔로우 관계가 아닌 사용자에 대한 언팔로우 요청 시 실패 검증")
    @Test
    public void unfollowUser_shouldFail_01() throws Exception {
      //given
      //when
      /*언팔로우 요청*/
      CommonResponse<Void> unfollowResponse = sendUnfollowUser(
          getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1), USER_ID, status().isNotFound());

      //then
      validateFailResponse(
          unfollowResponse, ErrorCode.FOLLOW_RELATION_NOT_FOUND
      );

    }

    /*[Case #2] PENDING 상태의 사용자를 언팔로우 요청 시 실패 검증*/
    @Order(2)
    @DisplayName("2. PENDING 상태의 사용자를 언팔로우 요청 시 실패 검증")
    @Test
    public void unfollowUser_shouldSuccess_01() throws Exception {
      //given
      /*데이터 삽입*/
      userFollowFixtureLoader.load(
          "/users/follow/unfollow/users.json",
          "/users/follow/unfollow/follows.json"
      );

      String accessToken = getAccessToken(PENDING_FOLLOW_FOLLOWER_EMAIL);

      //when
      /*언팔로우 요청*/
      CommonResponse<Void> unfollowResponse = sendUnfollowUser(accessToken, FOLLOWING_USER_ID,
          status().isNotFound());

      //then
      validateFailResponse(unfollowResponse, ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }

  }
}

