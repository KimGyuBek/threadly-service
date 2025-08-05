package com.threadly.user.controller.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.user.UserStatusType;
import com.threadly.user.follow.query.dto.GetFollowingsApiResponse;
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

@DisplayName("팔로잉 조회 요청 관련 API Test")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetFollowingsApiTest extends BaseFollowApiTest {

  /*팔로우 당하는 사용자 id*/
  private static final String TARGET_USER_ID = "target_user";

  /*팔로우 당하는 사용자 email*/
  private static final String TARGET_USER_EMAIL = "target_user@threadly.com";

  /*전체 팔로우 요청 수 */
  private static final int FOLLOW_REQUESTS_SIZE = 100;

  /*테스트 사용자 id*/
  private static final String TEST_USER_ID = "test_user";

  /*테스트 사용자 email*/
  private static final String TEST_USER_EMAIL = "test_user@threadly.com";

  /*
   * 1. 팔로잉중인 사용자가 없는 사용자의 팔로잉 목록 조회 요청 검증
   * 2. 팔로우 요청 후 해당 사용자가 팔로잉 목록에 포함되는지 검증
   * 3. 팔로우 요청 수락 대기중인 사용자가 팔로잉 목록에 포함되는지 검증
   * 4. 팔로우 취소 후 해당 사용자가 팔로잉 목록에 포함되는지 검증
   * 5. 팔로워 삭제 후 해당 사용자의 팔로잉 목록에 포함되는지 검증
   * 6. 팔로잉이 있는 사용자의 팔로잉 목록 전제 조회 검증
   * 7. 팔로잉 목록에서 비활성화 된 사용자가 포함되는지 검증
   * 8. 팔로잉 목록에서 탈퇴 된 사용자가 포함 되는지 검증
   * 9. 공개 계정인 사용자의 팔로잉 목록 전체 조회 검증
   * 10. 비공개 계정이면서 팔로우 상태인 사용자의 팔로잉 목록 조회 검증
   * 11. 비공개 계정이면서 팔로우 상태가 아닌 사용자의 팔로잉 목록 조회 불가 검증
   * */


  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /* 1. 팔로잉 중인 사용자가 없는 사용자의 팔로워 목록 조회 요청 검증*/
    @Order(1)
    @DisplayName("1. 팔로잉중인 사용자가 없는 사용자의 팔로워 목록 조회 요청 검증")
    @Test
    public void getFollowings_shouldSuccess_01() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      /*팔로잉 목록 조회 요청*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, null, null, null, 10, status().isOk()
      );

      //then
      assertThat(getFollowingsResponse.getData().followings()).isEmpty();
    }

    /*[Case #2]  팔로우 요청 후 해당 사용자가 팔로잉 목록에 포함되는지 검증 */
    @Order(2)
    @DisplayName("2. 팔로우 요청 후 해당 사용자가 팔로잉 목록에 포함되는지 검증")
    @Test
    public void getFollowings_shouldSuccess_02() throws Exception {
      //given
      userFixtureLoader.load("/users/profile/user.json");
      userFixtureLoader.load("/users/profile/user2.json");

      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      /*팔로우 요청*/
      sendFollowUserRequest(accessToken, USER2_ID, status().isOk());
      //when

      /*팔로잉 목록 조회 요청*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, null, null, null, FOLLOW_REQUESTS_SIZE, status().isOk()
      );

      //then
      assertThat(
          getFollowingsResponse.getData().followings().getFirst().following().userId()).isEqualTo(
          USER2_ID);
    }

    /*[Case #3]  팔로우 요청 수락 대기중인 사용자가 팔로앙 목록에 포함되지 않는지 검증  */
    @Order(3)
    @DisplayName("3. 팔로우 요청 수락 대기중인 사용자가 팔로잉 목록에 포함되는지 않는지 검증")
    @Test
    public void getFollowings_shouldSuccess_03() throws Exception {
      //given
      userFixtureLoader.load("/users/profile/user.json");
      userFixtureLoader.load("/users/profile/user2.json", UserStatusType.ACTIVE, true);

      /*로그인*/
      String accessToken = getAccessToken(USER_EMAIL);

      /*팔로우 요청*/
      sendFollowUserRequest(accessToken, USER2_ID, status().isOk());
      //when


      /*팔로잉 목록 조회 요청*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, null, null, null, FOLLOW_REQUESTS_SIZE, status().isOk()
      );

      //then
      assertThat(getFollowingsResponse.getData().followings()).isEmpty();
    }

    /*[Case #4]  팔로우 취소 후 해당 사용자가 팔로워 목록에 포함되는지 검증  */
    @Order(4)
    @DisplayName("4. 팔로우 취소 후 해당 사용자가 팔로워 목록에 포함되는지 검증")
    @Test
    public void getFollowings_shouldSuccess_04() throws Exception {
      //given

      //when

      //then

    }

    /*[Case #5]  팔로워 삭제 후 해당 사용자가 팔로워 목록에 포함되는지 검증 */
    @Order(5)
    @DisplayName("5. 팔로워 삭제 후 해당 사용자가 팔로워 목록에 포함되는지 검증")
    @Test
    public void getFollowings_shouldSuccess_05() throws Exception {
      //given

      //when

      //then

    }


    /*[Case #6]  팔로워잉 하는 사용자가 있는 사용자의 팔로워 목록 전제 조회 검증 */
    @Order(6)
    @DisplayName("6.팔로잉 하는 사용자가 있는 사용자의 팔로워 목록 전제 조회 검증")
    @Test
    public void getFollowings_shouldSuccess_06() throws Exception {
      //given
      /*데이터 로드*/
      userFixtureLoader.load("/users/follow/followings/user.json");
      userFollowFixtureLoader.load("/users/follow/followings/users.json",
          "/users/follow/followings/followings.json");

      /*로그인*/
      String accessToken = getAccessToken(TEST_USER_EMAIL);
      //when
      LocalDateTime cursorFollowedAt = null;
      String cursorFollowerId = null;
      int limit = 10;
      int size = 0;

      while (true) {
        CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
            accessToken, null, cursorFollowedAt, cursorFollowerId, limit, status().isOk()
        );
        size += getFollowingsResponse.getData().followings().size();

        /*마지막 페이지인 경우*/
        if (getFollowingsResponse.getData().nextCursor().cursorFollowedAt() == null) {
          break;
        }

        cursorFollowedAt = getFollowingsResponse.getData().nextCursor().cursorFollowedAt();
        cursorFollowerId = getFollowingsResponse.getData().nextCursor().cursorFollowingId();
      }

      //then
      assertThat(size).isEqualTo(FOLLOW_REQUESTS_SIZE);
    }

    /*[Case #7] 팔로잉 목록에서 비활성화 된 사용자가 포함되는지 검증 */
    @Order(7)
    @DisplayName("7. 팔로잉 목록에서 비활성화 된 사용자가 포함되는지 검증")
    @Test
    public void getFollowings_shouldSuccess_07() throws Exception {
      //given
      /*데이터 로드*/
      userFixtureLoader.load("/users/follow/followings/target-user.json", UserStatusType.INACTIVE);
      userFollowFixtureLoader.load(
          "/users/follow/followings/user.json",
          "/users/follow/followings/follow.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TEST_USER_EMAIL);

      //when
      /*팔로잉 목록 조회*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, null, null, null, FOLLOW_REQUESTS_SIZE, status().isOk());

      //then
      assertThat(getFollowingsResponse.getData().followings()).isEmpty();
    }

    /*[Case #8] 팔로워 목록에서 탈퇴처리 된 사용자가 포함되는지 검증 */
    @Order(8)
    @DisplayName("8. 팔로잉 목록에서 탈퇴처리 된 사용자가 포함되는지 검증")
    @Test
    public void getFollowings_shouldSuccess_08() throws Exception {
      userFixtureLoader.load("/users/follow/followings/target-user.json", UserStatusType.DELETED);
      userFollowFixtureLoader.load(
          "/users/follow/followings/user.json",
          "/users/follow/followings/follow.json"
      );

      /*로그인*/
      String accessToken = getAccessToken(TEST_USER_EMAIL);

      //when
      /*팔로잉 목록 조회*/
      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, null, null, null, FOLLOW_REQUESTS_SIZE, status().isOk());

      //then
      assertThat(getFollowingsResponse.getData().followings()).isEmpty();
    }


    /*[Case #9] 공개 계정인 다른 사용자의 팔로워 목록 전체 조회 검증 */
    @Order(9)
    @DisplayName("9. 공개 계정인 다른 사용자의 팔로워 목록 전체 조회 검증")
    @Test
    public void getFollowings_shouldSuccess_09() throws Exception {
      //given
      /*데이터 로드*/
      userFixtureLoader.load("/users/follow/followings/user.json");
      userFollowFixtureLoader.load("/users/follow/followings/users.json",
          "/users/follow/followings/followings.json");

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);
      //when
      LocalDateTime cursorFollowedAt = null;
      String cursorFollowerId = null;
      int limit = 10;
      int size = 0;

      while (true) {
        CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
            accessToken, TEST_USER_ID, cursorFollowedAt, cursorFollowerId, limit, status().isOk()
        );
        size += getFollowingsResponse.getData().followings().size();

        /*마지막 페이지인 경우*/
        if (getFollowingsResponse.getData().nextCursor().cursorFollowedAt() == null) {
          break;
        }

        cursorFollowedAt = getFollowingsResponse.getData().nextCursor().cursorFollowedAt();
        cursorFollowerId = getFollowingsResponse.getData().nextCursor().cursorFollowingId();
      }

      //then
      assertThat(size).isEqualTo(FOLLOW_REQUESTS_SIZE);
    }

    /*[Case #10] 비공개 계정이면서 팔로우 상태인 사용자의 팔로잉 목록 전체 조회 검증 */
    @Order(10)
    @DisplayName("10. 비공개 계정이면서 팔로우 상태인 사용자의 팔로잉 목록 전제 조회 검증")
    @Test
    public void getFollowings_shouldSuccess_10() throws Exception {
      //given
      /*데이터 로드*/
      userFixtureLoader.load("/users/follow/followings/target-user.json", UserStatusType.ACTIVE,
          true);
      userFollowFixtureLoader.load("/users/follow/followings/user.json",
          "/users/follow/followings/follow.json"
      );
      userFollowFixtureLoader.load("/users/follow/followings/users.json",
          "/users/follow/followings/followings.json");

      /*로그인*/
      String accessToken = getAccessToken(TEST_USER_EMAIL);
      //when
      LocalDateTime cursorFollowedAt = null;
      String cursorFollowerId = null;
      int limit = 10;
      int size = 0;

      while (true) {
        CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
            accessToken, TEST_USER_ID, cursorFollowedAt, cursorFollowerId, limit, status().isOk()
        );
        size += getFollowingsResponse.getData().followings().size();

        /*마지막 페이지인 경우*/
        if (getFollowingsResponse.getData().nextCursor().cursorFollowedAt() == null) {
          break;
        }

        cursorFollowedAt = getFollowingsResponse.getData().nextCursor().cursorFollowedAt();
        cursorFollowerId = getFollowingsResponse.getData().nextCursor().cursorFollowingId();
      }

      //then
      assertThat(size).isEqualTo(FOLLOW_REQUESTS_SIZE + 1);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 비공개 계정이면서 팔로우 상태가 아닌 사용자의 팔로잉 목록 전체 조회 실패 검증 */
    @Order(1)
    @DisplayName("1. 비공개 계정이면서 팔로우 상태가 아닌 사용자의 팔로잉 목록 전체 조회 실패 검증")
    @Test
    public void getFollowings_shouldFail_01() throws Exception {
      //given
      /*데이터 로드*/
      userFixtureLoader.load("/users/follow/followings/user.json", UserStatusType.ACTIVE, true);
      userFollowFixtureLoader.load("/users/follow/followings/users.json",
          "/users/follow/followings/followings.json");

      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);
      //when

      CommonResponse<GetFollowingsApiResponse> getFollowingsResponse = sendGetFollowingsRequest(
          accessToken, TEST_USER_ID, null, null, 10, status().isForbidden()
      );

      //then
      validateFailResponse(getFollowingsResponse, ErrorCode.USER_PROFILE_PRIVATE);
    }

  }
}

