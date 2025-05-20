package com.threadly.controller.post.comment.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 댓글 좋아요 생성 관련 테스트
 */
public class CreatePostCommentLikeApiTest extends BasePostApiTest {

  /**
   * likePostComment() 테스트
   *
   * @throws Exception
   */
  /*[Case #1] likePostComment - 댓글에 좋아요를 누르면 응답이 일치해야한다?*/
  @Test
  public void likePostComment_shouldSuccess() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String accessToken = getAccessToken(email);

    String postId = ACTIVE_COMMENTS.getFirst().get("postId");
    String commentId = ACTIVE_COMMENTS.getFirst().get("commentId");

    //when
    //then
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        accessToken, postId, commentId, status().isCreated());

    assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(commentId);
    assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(1);
  }

  /*[Case #2] likePostComment - ACTIVE 상태가 아닌 댓글에 좋아요를 누르면 실패해야함*/
  @DisplayName("")
  @Test
  public void likePostComment_shouldReturnFail() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String accessToken = getAccessToken(email);

    String postId = DELETED_COMMENTS.getFirst().get("postId");
    String commentId = DELETED_COMMENTS.getFirst().get("commentId");

    //when
    //then
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        accessToken, postId, commentId, status().isBadRequest());

    assertThat(likePostCommentResponse.isSuccess()).isFalse();
    assertThat(likePostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED.getCode());
  }

  /*[Case #3] likePostComment - 여러명의 사용자가 댓글에 좋아요를 누르면 응답이 likecount가 증가해야함?*/
  @Test
  public void likePostComment_shouldSuccess2() throws Exception {
    //given

    String postId = ACTIVE_COMMENTS.getFirst().get("postId");
    String commentId = ACTIVE_COMMENTS.getFirst().get("commentId");

    //when
    //then
    /*여러명의 사용자 로그인 후 좋아요 요청 전송*/
    for (int i = 1; i < VERIFIED_USER_EMAILS.size(); i++) {
          sendLikePostCommentRequest(
              getAccessToken(VERIFIED_USER_EMAILS.get(i)), postId, commentId, status().isCreated());
    }
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        getAccessToken(VERIFIED_USER_EMAILS.getFirst()), postId, commentId, status().isCreated());

    assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(commentId);
    assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(VERIFIED_USER_EMAILS.size());
  }

  /*[Case #4] likePostComment - 중복 좋아요 방지*/
  @Test
  public void likePostComment_shouldSuccess3() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String accessToken = getAccessToken(email);

    String postId = ACTIVE_COMMENTS.getFirst().get("postId");
    String commentId = ACTIVE_COMMENTS.getFirst().get("commentId");

    //when
    //then
    for (int i = 0; i < 100; i++) {
      sendLikePostCommentRequest(
          accessToken, postId, commentId, status().isCreated());
    }
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        accessToken, postId, commentId, status().isCreated());

    assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(commentId);
    assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(1);
  }
}
