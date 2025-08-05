package com.threadly.user.controller.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.FollowStatusType;
import com.threadly.user.UserStatusType;
import com.threadly.user.follow.command.dto.FollowUserApiResponse;
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
 * follow 관련 API 테스트
 */
@DisplayName("Follow 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class FollowApiTest extends BaseFollowApiTest {

  /*
   * 1. 공개 계정을 팔로우 요청하는 경우
   * 2. 비공개 계정을 팔로우 요청 하는 경우
   * 3. 존재하지 않는 사용자를 팔로우 요청 하는 경우
   * 4. 탈퇴한 사용자를 팔로우 요청하는 경우
   * 5. 비활성화 상태의 사용자를 팔로우 요청하는 경우
   * 6. 자신에게 팔로우 요청을 하는 경우 실패 검증
   * */

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("Follow 테스트")
  @Nested
  class FollowTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 공개 계정을 팔로우 요청하는 경우 검증*/
      @Order(1)
      @DisplayName("1. 공개 계정을 팔로우 요청하는 경우 검증")
      @Test
      public void followUser_shouldSuccess_01() throws Exception {
        //given
        /*사용자 데이터 삽입*/
        userFixtureLoader.load("/users/profile/user.json", UserStatusType.ACTIVE, false);

        /*로그인*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isOk());

        //then
        assertThat(followUserResponse.getData().followStatusType()).isEqualTo(
            FollowStatusType.APPROVED);
      }

      /*[Case #2] 비공개 계정을 팔로우 요청하는 경우 검증*/
      @Order(2)
      @DisplayName("2. 비공개 계정을 팔로우 요청하는 경우 검증")
      @Test
      public void followUser_shouldSuccess_02() throws Exception {
        //given
        /*사용자 데이터 삽입*/
        userFixtureLoader.load("/users/profile/user.json", UserStatusType.ACTIVE, true);

        /*로그인*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isOk());

        //then
        assertThat(followUserResponse.getData().followStatusType()).isEqualTo(
            FollowStatusType.PENDING);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 사용자를 팔로우 요청하는 경우 실패 검증*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 사용자를 팔로우 요청하는 경우 실패 검증")
      @Test
      public void followUser_shouldFail_01() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isNotFound());

        //then
        validateFailResponse(followUserResponse, ErrorCode.USER_NOT_FOUND);
      }

      /*[Case #2] 탈퇴 처리된 사용자를 팔로우 요청하는 경우 실패 검증*/
      @Order(2)
      @DisplayName("2. 탈퇴 처리된 사용자를 팔로우 요청하는 경우 실패 검증")
      @Test
      public void followUser_shouldFail_02() throws Exception {
        //given
        /*사용자 데이터 삽입*/
        userFixtureLoader.load("/users/profile/user.json", UserStatusType.DELETED, false);

        /*로그인*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isForbidden());

        //then
        validateFailResponse(followUserResponse, ErrorCode.USER_ALREADY_DELETED);
      }

      /*[Case #3] 비활성화 처리된 사용자를 팔로우 요청하는 경우 실패 검증*/
      @Order(3)
      @DisplayName("3. 비활성화 처리 된 사용자를 팔로우 요청하는 경우 실패 검증")
      @Test
      public void followUser_shouldFail_03() throws Exception {
        //given
        /*사용자 데이터 삽입*/
        userFixtureLoader.load("/users/profile/user.json", UserStatusType.INACTIVE, false);

        /*로그인*/
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isForbidden());

        //then
        validateFailResponse(followUserResponse, ErrorCode.USER_INACTIVE);
      }

      /*[Case #4] 자신에게 팔로우 요청을 하는 경우 실패 검증*/
      @Order(4)
      @DisplayName("4. 자신에게 팔로우 요청을 하는 경우 실패 검증")
      @Test
      public void followUser_shouldFail_04() throws Exception {
        //given
        /*사용자 데이터 삽입*/
        userFixtureLoader.load("/users/profile/user.json", UserStatusType.ACTIVE, false);

        /*로그인*/
        String accessToken = getAccessToken(USER_EMAIL);

        //when
        /*팔로우 요청*/
        CommonResponse<FollowUserApiResponse> followUserResponse = sendFollowUserRequest(
            accessToken, USER_ID, status().isBadRequest());

        //then
        validateFailResponse(followUserResponse, ErrorCode.SELF_FOLLOW_REQUEST_NOT_ALLOWED);
      }
    }
  }


}
