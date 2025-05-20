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
  /*[Case #1] likePostComment - 이메일 인증된 사용자가 ACTIVE 상태의 댓글에 좋아요를 누를경우 성공해야한다*/
  @DisplayName("게시글 댓글 좋아요 생성 - 정상 요청 시 201 Created, 좋아요 수 = 1 반환해야 한다")
  @Test
  public void likePostComment_shouldReturnCreated_whenUserLikesActiveComment() throws Exception {
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

  /*[Case #2] likePostComment - ACTIVE 상태가 아닌 댓글에 좋아요를 누르면 400 Bad Request, 실패해야한다*/
  @DisplayName("게시글 댓글 좋아요 생성 - 비활성 댓글(BLOCKED/DELETED)에는 좋아요 불가")
  @Test
  public void likePostComment_shouldReturnBadRequest_whenCommentNotActive() throws Exception {
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

  /*[Case #3] likePostComment - 여러 사용자가 댓글에 좋아요를 누를 경우 likeCount가 누적되어 증가해야 한다*/
  @DisplayName("게시글 댓글 좋아요 생성 - 여러 사용자가 좋아요 누를 경우 likeCount 누적")
  @Test
  public void likePostComment_shouldAccumulateLikeCount_whenMultipleUserLike() throws Exception {
    //given

    String postId = ACTIVE_COMMENTS.getFirst().get("postId");
    String commentId = ACTIVE_COMMENTS.getFirst().get("commentId");

    //when
    //then
    /*여러명의 사용자 로그인 후 좋아요 요청 전송*/
    for (int i = 1; i < VERIFIED_USER_EMAILS.size(); i++) {
      assertThat(
          sendLikePostCommentRequest(
              getAccessToken(VERIFIED_USER_EMAILS.get(i)), postId, commentId, status().isCreated()).isSuccess()).isTrue();
    }
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        getAccessToken(VERIFIED_USER_EMAILS.getFirst()), postId, commentId, status().isCreated());

    assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(commentId);
    assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(VERIFIED_USER_EMAILS.size());
  }

  /*[Case #4] likePostComment - 동일 사용자가 같은 댓글에 여러번 좋아요를 눌러도 멱등하게 처리되어야 함*/
  @DisplayName("게시글 댓글 좋아요 생성 - 동일 사용자가 중복 좋아요 요청 시 멱등하게 처리됨")
  @Test
  public void likePostComment_shouldBeIdempotent_whenUserLikesSameCommentMultipleTimes() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String accessToken = getAccessToken(email);

    String postId = ACTIVE_COMMENTS.getFirst().get("postId");
    String commentId = ACTIVE_COMMENTS.getFirst().get("commentId");

    //when
    //then
    for (int i = 0; i < 2; i++) {
      assertThat(
      sendLikePostCommentRequest(
          accessToken, postId, commentId, status().isCreated()).isSuccess()).isTrue();
    }
    CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
        accessToken, postId, commentId, status().isCreated());

    assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(commentId);
    assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(1);
  }
}
