package com.threadly.controller.post.comment.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.like.comment.LikePostCommentApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 좋아요 삭제 관련 Test
 */
public class DeletePostCommentLikeApiTest extends BasePostApiTest {

  /**
   * cancelPostCommentLike() 테스트
   *
   * @throws Exception
   */
  /*[Case #1] cancelPostCommentLike
  - 댓글에 이미 좋아요를 누른 상태에서 좋아요 취소를 요청하면 해당 좋아요가 제거되고 좋아요 수가 감소되어야 한다*/
  @DisplayName("게시글 댓글 좋아요 취소 - 기존에 좋아요한 댓글에 대해 정상적으로 좋아요 취소하면 좋아요 수가 감소되어야 한다")
  @Test
  public void cancelPostCommentLike_shouldCancelLikeAndDecreaseLikeCountWhenAlreadyLiked()
      throws Exception {
    //given
    String email = EMAIL_VERIFIED_USER_1;

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    String postId = ACTIVE_COMMENTS.get(0).get("postId");
    String commentId = ACTIVE_COMMENTS.get(0).get("commentId");

    /*댓글 좋아요 요청*/
    sendLikePostCommentRequest(
        accessToken,
        postId,
        commentId,
        status().isOk()
    );

    //when
    //then
    /*좋아요 취소 요청*/
    CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
        accessToken, postId, commentId, status().isNoContent()
    );

    assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(0);
  }

  /*[Case #2] cancelPostCommentLike
  - 사용자가 댓글에 대해 여러 번 좋아요 취소 요청을 보내도 좋아요 수는 0으로 유지되고 에러 없이 정상 응답 되어야 한다*/
  @DisplayName("게시글 댓글 좋아요 취소 - 좋아요 취소를 여러번 요청해도 응답은 멱등해야 한다")
  @Test
  public void cancelPostCommentLike_shouldBeIdempotentWhenCancelLikeMultipleTimes() throws Exception {
    //given
    String email = EMAIL_VERIFIED_USER_1;

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    String postId = ACTIVE_COMMENTS.get(0).get("postId");
    String commentId = ACTIVE_COMMENTS.get(0).get("commentId");

    /*댓글 좋아요 요청*/
    sendLikePostCommentRequest(
        accessToken,
        postId,
        commentId,
        status().isOk()
    );

    //when
    for (int i = 0; i < 2; i++) {
      sendDeletePostCommentLikeRequest(
          accessToken, postId, commentId, status().isNoContent()
      );
    }

    //then
    /*좋아요 취소 요청*/
    CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
        accessToken, postId, commentId, status().isNoContent()
    );

    assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(0);
  }


  /*[Case #3] cancelPostCommentLike - 좋아요를 누르지 않은 사용자가 취소 요청을 보낼 경우에도 멱등해야한다*/
@DisplayName("게시글 댓글 좋아요 취소 - 사용자가 좋아요를 누르지 않은 상태에서 취소 요청을 보내도 멱등하게 성공해야한다")
@Test
public void cancelPostCommentLike_shouldNotFailWhenUserDidNotLikeBefore() throws Exception {
  //given
  String email = EMAIL_VERIFIED_USER_1;

  /*로그인 요청*/
  String accessToken = getAccessToken(email);

  String postId = ACTIVE_COMMENTS.get(0).get("postId");
  String commentId = ACTIVE_COMMENTS.get(0).get("commentId");

  //when
  //then
  /*좋아요 취소 요청*/
  CommonResponse<LikePostCommentApiResponse> deletePostCommentLikeResponse = sendDeletePostCommentLikeRequest(
      accessToken, postId, commentId, status().isNoContent()
  );

  assertThat(deletePostCommentLikeResponse.getData().likeCount()).isEqualTo(0);
}
  /*좋아요를 누르지 않은 사용자가 좋아요 취소 요청을 보낼 경우*/
}
