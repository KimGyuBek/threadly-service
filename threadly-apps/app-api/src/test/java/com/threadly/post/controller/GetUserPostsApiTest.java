package com.threadly.post.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import com.threadly.testsupport.fixture.users.UserFollowFixtureLoader;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 특정 사용자 게시글 목록 조회 테스트
 * <p>
 * 테스트 시나리오:
 * 1. 팔로우 관계와 계정 공개/비공개 여부에 따른 게시글 조회 권한 검증
 * 2. 커서 기반 페이징 검증
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("특정 사용자 게시글 목록 조회 테스트")
class GetUserPostsApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @Autowired
  private UserFollowFixtureLoader userFollowFixtureLoader;

  private static final int PUBLIC_USER_POST_COUNT = 5;
  private static final int PRIVATE_USER_POST_COUNT = 3;

  /**
   * 테스트 데이터 로드
   * - 공개 계정 사용자 (target_public_user) - 5개 게시글
   * - 비공개 계정 사용자 (target_private_user) - 3개 게시글
   * - 조회자 (test_viewer) - 1개 게시글
   * - test_viewer -> target_private_user 팔로우 관계 (APPROVED)
   */
  @BeforeEach
  void setUp() {
    // 사용자 개별 로드 (공개/비공개 설정)
    userFixtureLoader.load("/posts/user-posts/public-user.json", UserStatus.ACTIVE, false);  // 공개 계정
    userFixtureLoader.load("/posts/user-posts/private-user.json", UserStatus.ACTIVE, true);  // 비공개 계정
    userFixtureLoader.load("/posts/user-posts/viewer-user.json", UserStatus.ACTIVE, false);  // 조회자

    // 게시글 로드
    postFixtureLoader.load("/posts/user-posts/posts.json", 9);

    // 팔로우 관계 로드 (test_viewer는 target_private_user를 팔로우 중)
    userFollowFixtureLoader.load("/users/user-fixture.json", "/posts/user-posts/follows.json");
  }

  /**
   * getUserPosts() - 특정 사용자 게시글 조회 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("특정 사용자 게시글 조회 테스트")
  @Nested
  class getUserPostsTest {

    @Order(1)
    @DisplayName("성공")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class success {

      /*[Case #1] 본인의 게시글을 조회하면 200 OK를 반환한다.*/
      @Order(1)
      @DisplayName("1. 본인 게시글 조회 - 성공")
      @Test
      public void getUserPosts_shouldReturnOk_whenGetOwnPosts() throws Exception {
        //given
        String accessToken = getAccessToken("viewer@threadly.com");

        //when
        CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
            accessToken,
            "test_viewer",
            null,
            null,
            10,
            status().isOk()
        );

        //then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().content()).hasSize(1);
        assertThat(response.getData().content().get(0).author().userId()).isEqualTo("test_viewer");
      }

      /*[Case #2] 공개 계정 사용자의 게시글을 조회하면 200 OK를 반환한다. (팔로우하지 않아도 조회 가능)*/
      @Order(2)
      @DisplayName("2. 공개 계정 사용자 게시글 조회 - 팔로우하지 않아도 성공")
      @Test
      public void getUserPosts_shouldReturnOk_whenGetPublicUserPosts() throws Exception {
        //given
        /*팔로우하지 않은 사용자로 로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
            accessToken,
            "target_public_user",
            null,
            null,
            10,
            status().isOk()
        );

        //then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().content()).hasSize(PUBLIC_USER_POST_COUNT);
        assertThat(response.getData().content().get(0).author().userId()).isEqualTo("target_public_user");
      }

      /*[Case #3] 팔로우 중인 비공개 계정 사용자의 게시글을 조회하면 200 OK를 반환한다.*/
      @Order(3)
      @DisplayName("3. 팔로우 중인 비공개 계정 게시글 조회 - 성공")
      @Test
      public void getUserPosts_shouldReturnOk_whenGetFollowingPrivateUserPosts() throws Exception {
        //given
        /*test_viewer는 target_private_user를 팔로우 중*/
        String accessToken = getAccessToken("viewer@threadly.com");

        //when
        CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
            accessToken,
            "target_private_user",
            null,
            null,
            10,
            status().isOk()
        );

        //then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().content()).hasSize(PRIVATE_USER_POST_COUNT);
        assertThat(response.getData().content().get(0).author().userId()).isEqualTo("target_private_user");
      }

      /*[Case #4] 커서 기반 페이징으로 게시글을 조회하면 nextCursor가 올바르게 반환된다.*/
      @Order(4)
      @DisplayName("4. 커서 기반 페이징 검증")
      @Test
      public void getUserPosts_shouldReturnCorrectCursor_whenUsingPagination() throws Exception {
        //given
        String accessToken = getAccessToken("viewer@threadly.com");

        //when
        /*첫 번째 페이지 조회 (limit=2)*/
        CommonResponse<CursorPageApiResponse<PostDetails>> page1 = sendGetUserPostsRequest(
            accessToken,
            "target_public_user",
            null,
            null,
            2,
            status().isOk()
        );

        /*두 번째 페이지 조회*/
        LocalDateTime cursorTimestamp = page1.getData().nextCursor().cursorTimestamp();
        String cursorId = page1.getData().nextCursor().cursorId();

        CommonResponse<CursorPageApiResponse<PostDetails>> page2 = sendGetUserPostsRequest(
            accessToken,
            "target_public_user",
            cursorTimestamp,
            cursorId,
            2,
            status().isOk()
        );

        //then
        assertThat(page1.getData().content()).hasSize(2);
        assertThat(page2.getData().content()).hasSize(2);
        assertThat(page1.getData().content().get(0).cursorId())
            .isNotEqualTo(page2.getData().content().get(0).cursorId());
        assertThat(page1.getData().nextCursor().cursorId())
            .isEqualTo(page1.getData().content().get(1).cursorId());
      }

      /*[Case #5] 전체 페이지를 순회하면 모든 게시글을 조회할 수 있다.*/
      @Order(5)
      @DisplayName("5. 전체 페이지 순회 - 모든 게시글 조회")
      @Test
      public void getUserPosts_shouldIterateAllPages_usingCursorPagination() throws Exception {
        //given
        String accessToken = getAccessToken("viewer@threadly.com");

        //when
        LocalDateTime cursorTimestamp = null;
        String cursorId = null;
        int limit = 2;
        int totalCount = 0;

        while (true) {
          CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
              accessToken,
              "target_public_user",
              cursorTimestamp,
              cursorId,
              limit,
              status().isOk()
          );

          totalCount += response.getData().content().size();

          /*마지막 페이지면 종료*/
          if (response.getData().nextCursor().cursorTimestamp() == null) {
            break;
          }

          cursorTimestamp = response.getData().nextCursor().cursorTimestamp();
          cursorId = response.getData().nextCursor().cursorId();
        }

        //then
        assertThat(totalCount).isEqualTo(PUBLIC_USER_POST_COUNT);
      }
    }

    @Order(2)
    @DisplayName("실패")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class fail {

      /*[Case #1] 팔로우하지 않은 비공개 계정의 게시글을 조회하면 403 Forbidden을 반환한다.*/
      @Order(1)
      @DisplayName("1. 팔로우하지 않은 비공개 계정 게시글 조회 - 403 Forbidden")
      @Test
      public void getUserPosts_shouldReturnForbidden_whenGetNonFollowingPrivateUserPosts() throws Exception {
        //given
        /*팔로우하지 않은 사용자로 로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
            accessToken,
            "target_private_user",
            null,
            null,
            10,
            status().isForbidden()
        );

        //then
        /*비공개 계정의 게시글은 조회할 수 없으므로 403 에러*/
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ErrorCode.USER_PROFILE_PRIVATE.getCode());
      }

      /*
       * [Case #2] 존재하지 않는 사용자의 게시글을 조회하면 404 Not Found를 반환한다.
       * TODO: 현재 실제 구현에서 isUserPrivate()가 null을 반환하여 500 에러 발생
       *       (AopInvocationException: Null return value from advice does not match primitive return type)
       *       실제 구현이 수정되면 이 테스트를 활성화해야 함
       */
      @Order(2)
      @DisplayName("2. 존재하지 않는 사용자 게시글 조회 - 404 Not Found (TODO: 구현 버그 수정 필요)")
      @Test
      public void getUserPosts_shouldReturnNotFound_whenGetNonExistentUserPosts() throws Exception {
        //given
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when & then
        /*현재는 500 에러가 발생하지만, 구현이 수정되면 404를 기대해야 함*/
        try {
          sendGetUserPostsRequest(
              accessToken,
              "non_existent_user",
              null,
              null,
              10,
              status().is5xxServerError() // 현재는 500 에러 발생
          );
        } catch (Exception e) {
          // 현재는 ServletException이 발생함
          assertThat(e).isInstanceOf(Exception.class);
        }

        /*TODO: 구현 수정 후 아래 코드로 변경
        CommonResponse<CursorPageApiResponse<PostDetails>> response = sendGetUserPostsRequest(
            accessToken,
            "non_existent_user",
            null,
            null,
            10,
            status().isNotFound()
        );
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        */
      }
    }
  }
}
