package com.threadly.post.controller.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.like.post.LikePostApiResponse;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import java.util.List;
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
 * 게시글 좋아요 생성 관련 API 테스트
 * <p>
 * 테스트 데이터 {/test/resources/fixtures/posts/likes/like-post/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class LikePostApiTest extends BasePostApiTest {

  // 게시글 ID (status별 분류)
  public static final String ACTIVE_POST_ID = "active_post_id";
  public static final String ARCHIVED_POST_ID = "archived_post_id";
  public static final String BLOCKED_POST_ID = "blocked_post_id";
  public static final String DELETED_POST_ID = "deleted_post_id";

  // 게시글 작성자 이메일
  public static final String POST_WRITER_EMAIL = "writer@threadly.com";

  // 좋아요를 누를 사용자 이메일 목록
  public static final List<String> USERS = List.of(
      "sunset_gazer1@threadly.com",
      "sky_gazer2@threadly.com",
      "book_worm3@threadly.com",
      "beach_bum4@threadly.com",
      "early_bird5@threadly.com",
      "mountain_hiker6@threadly.com"
  );

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postFixtureLoader.load("/posts/likes/like-post/user.json", "/posts/likes/like-post/post.json");
  }

  /**
   * likePost() 테스트
   */

  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 좋아요 테스트")
  @Nested
  class likePost {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] likePost - ACTIVE 상태의 게시글에 좋아요를 눌렀을 경우 성공해야한다*/
      @Order(1)
      @DisplayName("1. 정상 요청시 likeCount 반환 검증")
      @Test
      public void likePost_shouldReturnCreated_whenUserLikesActivePost() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(USERS.getFirst());

        //when
        //then
        /*게시글 좋아요 요청 전송*/
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            ACTIVE_POST_ID,
            status().isOk()
        );
        assertThat(likePostResponse.getData().likeCount()).isEqualTo(1);
      }

      /*[Case #2] likePost - 동일 사용자가 같은 게시글에 여러번 좋아요 요청을 해도 멱등하게 처리되어야 한다*/
      @Order(2)
      @DisplayName("2. 동일 사용자가 중복 좋아요 요청 시 멱등하게 처리되는지 검증")
      @Test
      public void likePost_shouldBeIdempotent_whenUserLikesSamePostMultipleTimes()
          throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(USERS.getFirst());

        //when
        //then
        /*게시글 좋아요 요청 전송*/
        for (int i = 0; i < 3; i++) {
          assertThat(
              sendLikePostRequest(accessToken, ACTIVE_POST_ID,
                  status().isOk()).isSuccess()).isTrue();
        }
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            ACTIVE_POST_ID,
            status().isOk()
        );
        assertThat(likePostResponse.getData().likeCount()).isEqualTo(1);
      }

      /*[Case #2] likePost - 여러 사용자가 댓글에 좋아요를 누를 경우 likeCount가 누적되어 증가해야 한다*/
      @DisplayName("2. 여러 사용자가 게시글에 좋아요를 누를 경우 likeCount 누적되는지 검증")
      @Test
      public void likePost_shouldAccumulateLikeCount_whenMultipleUserLike() throws Exception {
        //given
        //when
        //then
        /*게시글 좋아요 요청 전송*/
        for (int i = 1; i < USERS.size(); i++) {
          assertThat(sendLikePostRequest(
              getAccessToken(USERS.get(i)), ACTIVE_POST_ID, status().isOk()
          ).isSuccess()).isTrue();
        }
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            getAccessToken(EMAIL_VERIFIED_USER_1),
            ACTIVE_POST_ID,
            status().isOk()
        );
        assertThat(likePostResponse.getData().likeCount()).isEqualTo(USERS.size());
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] likePost - DELETED 상태의 게시글에 좋아요를 누를경우 400 Bad Request, 실패해야한다 */
      @Order(1)
      @DisplayName("1. DELETED 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust ")
      @Test
      public void likePost_shouldReturnBadRequest_whenPostDeleted() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(USERS.getFirst());

        //when
        //then
        /*게시글 좋아요 요청 전송*/
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            DELETED_POST_ID,
            status().isBadRequest()
        );
        assertThat(likePostResponse.getCode()).isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }

      @Order(2)
      @DisplayName("2. BLOCKED 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust ")
      @Test
      public void likePost_shouldReturnBadRequest_whenPostBlocked() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(USERS.getFirst());

        //when
        //then
        /*게시글 좋아요 요청 전송*/
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            BLOCKED_POST_ID,
            status().isBadRequest()
        );
        assertThat(likePostResponse.getCode()).isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }

      @Order(3)
      @DisplayName("3. ARCHIVE 상태의 게시글에 좋아요 요청 시 400 Bad Reqeust ")
      @Test
      public void likePost_shouldReturnBadRequest_whenPostArchive() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(USERS.getFirst());

        //when
        //then
        /*게시글 좋아요 요청 전송*/
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            ARCHIVED_POST_ID,
            status().isBadRequest()
        );
        assertThat(likePostResponse.getCode()).isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }
    }
  }


}
