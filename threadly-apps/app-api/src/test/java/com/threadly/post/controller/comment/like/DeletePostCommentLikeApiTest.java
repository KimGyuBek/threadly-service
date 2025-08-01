package com.threadly.post.controller.comment.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.like.comment.LikePostCommentApiResponse;
import com.threadly.testsupport.fixture.posts.PostCommentLikeFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 게시글 댓글 좋아요 삭제 관련 Test
 * <p>
 * 테스트 데이터 = { "/posts/comments/likes/delete-comment-like/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class DeletePostCommentLikeApiTest extends BasePostApiTest {

  @Autowired
  private PostCommentLikeFixtureLoader postCommentLikeFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postCommentLikeFixtureLoader.load(
        "/posts/comments/likes/delete-comment-like/user.json",
        "/posts/comments/likes/delete-comment-like/post.json",
        "/posts/comments/likes/delete-comment-like/post-comment.json",
        "/posts/comments/likes/delete-comment-like/post-comment-like.json"
    );
  }

  // 댓글 ID (상태별)
  public static final String COMMENT_WITH_LIKES = "cmt_active_001";
  public static final String COMMENT_WITHOUT_LIKES = "cmt_active_002";

  // 게시글 ID
  public static final String POST_ID = "active_post_id";

  // 좋아요를 누른 사용자 이메일
  public static final String COMMENT_LIKER_EMAIL = "commentliker@threadly.com";

  // 좋아요를 누르지 않은 사용자 이메일
  public static final String COMMENT_NOT_LIKER_EMAIL = "commentNotLiker@threadly.com";

  /**
   * cancelPostCommentLike() 테스트
   */
  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 좋아요 취소 테스트")
  @Nested
  class cancelPostCommentLikeTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {


      /*[Case #1] cancelPostCommentLike
      - 댓글에 이미 좋아요를 누른 상태에서 좋아요 취소를 요청하면 해당 좋아요가 제거되고 좋아요 수가 감소되어야 한다*/
      @Order(1)
      @DisplayName("1. 좋아요를 누른 사용자가 취소 요청 시 좋아요가 제거되는지 검증")
      @Test
      public void cancelPostCommentLike_shouldCancelLikeAndDecreaseLikeCountWhenAlreadyLiked()
          throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(COMMENT_LIKER_EMAIL);

        //when
        //then
        /*좋아요 취소 요청*/
        CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
            accessToken, POST_ID, COMMENT_WITH_LIKES, status().isNoContent()
        );

        assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(0);
      }

      /*[Case #2] cancelPostCommentLike
      - 사용자가 댓글에 대해 여러 번 좋아요 취소 요청을 보내도 좋아요 수는 0으로 유지되고 에러 없이 정상 응답 되어야 한다*/
      @Order(2)
      @DisplayName("2. 사용자가 여러번 좋아요 취소 요청을 보내도 멱등한지 검증")
      @Test
      public void cancelPostCommentLike_shouldBeIdempotentWhenCancelLikeMultipleTimes()
          throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(COMMENT_LIKER_EMAIL);

        //when
        for (int i = 0; i < 2; i++) {
          sendDeletePostCommentLikeRequest(
              accessToken, POST_ID, COMMENT_WITH_LIKES, status().isNoContent()
          );
        }

        //then
        /*좋아요 취소 요청*/
        CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
            accessToken, POST_ID, COMMENT_WITH_LIKES, status().isNoContent()
        );

        assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(0);
      }


      /*[Case #3] cancelPostCommentLike - 좋아요를 누르지 않은 사용자가 취소 요청을 보낼 경우에도 멱등해야한다*/
      @Order(3)
      @DisplayName("3. 사용자가 좋아요를 누르지 않은 상태에서 취소 요청 시 멱등한지 검증")
      @Test
      public void cancelPostCommentLike_shouldNotFailWhenUserDidNotLikeBefore() throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(COMMENT_NOT_LIKER_EMAIL);

        //when
        //then
        /*좋아요 취소 요청*/
        CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
            accessToken, POST_ID, COMMENT_WITH_LIKES, status().isNoContent()
        );

        assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(1);
      }
    }
  }
}