package com.threadly.controller.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 상태 변경 관련 API 테스트
 */
public class UpdatePostCommentApiTest extends BasePostApiTest {

  /**
   * deletePostComment() 테스트
   */
  /*[Case #1] deletePostComment - 댓글 작성자가 ACTIVE 상태 댁글 삭제 시 204 No Content 응답*/
  @DisplayName("게시글 댓글 삭제 -  댓글 작성자가 ACTIVE 상태 댁글 삭제 시 204 No Content 응답")
  @Test
  public void deletePostComment_shouldReturn204_whenCommentIsActive() throws Exception {
    //given
    String email = ACTIVE_COMMENTS.get(0).get("userEmail");
    String postId = ACTIVE_COMMENTS.get(0).get("postId");
    String postCommentId = ACTIVE_COMMENTS.get(0).get("commentId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청*/
    sendDeletePostCommentRequest(accessToken, postId,
        postCommentId, status().isNoContent());
  }


  /*[Case #2] deletePostComment - 댓글 작성자가 비활성 게시글에 댓글 삭제 요청 시 400 Bad Request 응답*/
  @DisplayName("게시글 댓글 삭제 - 댓글 작성자가 비활성 게시글에 댓글 삭제 요청 시 400 Bad Request 응답")
  @Test
  public void deletePostComment_shouldReturn400_whenPostIsNotActive() throws Exception {
    //given
    String email = ACTIVE_COMMENTS.get(0).get("userEmail");
    String postId = DELETED_POSTS.get(0).get("postId");
    String postCommentId = ACTIVE_COMMENTS.get(0).get("commentId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청*/
    CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
        postId,
        postCommentId, status().isBadRequest());

    assertThat(deletePostCommentResponse.isSuccess()).isFalse();
    assertThat(deletePostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_PARENT_POST_INACTIVE.getCode());
  }

  /*[Case #3] deletePostComment - 댓글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden 응답 */
  @DisplayName("게시글 댓글 삭제 - 댓글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden 응답")
  @Test
  public void deletePostComment_shouldReturn403_whenCommentWriterNotEqualsUser()
      throws Exception {
    //given
    String email = ACTIVE_COMMENTS.get(1).get("userEmail");
    String postId = ACTIVE_COMMENTS.get(0).get("postId");
    String postCommentId = ACTIVE_COMMENTS.get(0).get("commentId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청*/
    CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
        postId,
        postCommentId, status().isForbidden());

    assertThat(deletePostCommentResponse.isSuccess()).isFalse();
    assertThat(deletePostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_DELETE_FORBIDDEN.getCode());
  }

  /*[Case #4] deletePostComment -  이미 삭제된 댓글 삭제 요청 시 400 Bad Request 응답 */
  @DisplayName("게시글 댓글 삭제 - 이미 삭제된 댓글 삭제 요청 시 400 Bad Request 응답")
  @Test
  public void deletePostComment_shouldReturn400_whenCommentIsDeleted() throws Exception {
    //given
    String email = DELETED_COMMENTS.get(0).get("userEmail");
    String postId = DELETED_COMMENTS.get(0).get("postId");
    String postCommentId = DELETED_COMMENTS.get(0).get("commentId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청*/
    CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
        postId,
        postCommentId, status().isBadRequest());

    assertThat(deletePostCommentResponse.isSuccess()).isFalse();
    assertThat(deletePostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_ALREADY_DELETED.getCode());
  }

  /*[Case #5] deletePostComment - BLOCKED 상태 댓글 삭제 시 400 Bad Request 응답 */
  @DisplayName("게시글 댓글 삭제 - BLOCKED 상태 댓글 삭제 시 400 Bad Request 응답")
  @Test
  public void deletePostComment_shouldReturn400_whenCommentIsBlocked() throws Exception {
    //given
    String email = BLOCKED_COMMENTS.get(0).get("userEmail");
    String postId = BLOCKED_COMMENTS.get(0).get("postId");
    String postCommentId = BLOCKED_COMMENTS.get(0).get("commentId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청*/
    CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
        postId,
        postCommentId, status().isBadRequest());

    assertThat(deletePostCommentResponse.isSuccess()).isFalse();
    assertThat(deletePostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_DELETE_BLOCKED.getCode());
  }
}
