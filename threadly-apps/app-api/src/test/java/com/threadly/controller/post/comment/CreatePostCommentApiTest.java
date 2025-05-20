package com.threadly.controller.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.comment.response.CreatePostCommentApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 생성 관련 API 테스트
 */
public class CreatePostCommentApiTest extends BasePostApiTest {

  /**
   * createPostcomment() 테스트
   */
  /*[Case #1] createPostComment()- ACTIVE 상태인 게시글에 댓글을 작성할 경우 성공해야 한다*/
  @DisplayName("게시글 댓글 생성 - 게시글 상태가 ACTIVE 경우 댓글 생성에 성공해야 한다")
  @Test
  public void createPostComment_shouldSucceed_whenPostIsActive() throws Exception {
    //given

    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    String postId = ACTIVE_POSTS.get(0).get("postId");
    String content = "게시글 댓글 생성";

    /*댓글 추가 요청 전송*/
    CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
        accessToken, postId, content, status().isCreated()
    );

    assertThat(createPostCommentResponse.getData().content()).isEqualTo(content);
  }

  /*[Case #2] createPostComment()- DELETED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
  @DisplayName("게시글 댓글 생성 - 게시글 상태가 DELETED인 경우 댓글 생성에 실패해야 한다")
  @Test
  public void createPostComment_shouldReturnBadRequest_whenPostIsActive() throws Exception {
    //given

    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    String postId = DELETED_POSTS.get(0).get("postId");
    String content = "게시글 댓글 생성";

    /*댓글 추가 요청 전송*/
    CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
        accessToken, postId, content, status().isBadRequest()
    );

    assertThat(createPostCommentResponse.isSuccess()).isFalse();
    assertThat(createPostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_ALREADY_DELETED.getCode());
  }

  /*[Case #3] createPostComment()- BLOCKED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
  @DisplayName("게시글 댓글 생성 - 게시글 상태가 BLOCKED인 경우 댓글 생성에 실패야해 한다")
  @Test
  public void createPostComment_shouldReturnBadRequest_whenPostIsBlocked() throws Exception {
    //given

    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    String postId = BLOCKED_POSTS.get(0).get("postId");
    String content = "게시글 댓글 생성";

    /*댓글 추가 요청 전송*/
    CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
        accessToken, postId, content, status().isBadRequest()
    );

    assertThat(createPostCommentResponse.isSuccess()).isFalse();
    assertThat(createPostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_BLOCKED.getCode());
  }

  /*[Case #4]  createPostComment() - ARCHIVED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
  @DisplayName("게시글 댓글 생성 - 게시글 상태가 ARCHIVED일 경우 댓글 생성에 실패야하 한다")
  @Test
  public void createPostComment_shouldReturnBadRequest_whenPostIsArchived() throws Exception {
    //given

    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    String postId = ARCHIVED_POSTS.get(0).get("postId");
    String content = "게시글 댓글 생성";

    /*댓글 추가 요청 전송*/
    CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
        accessToken, postId, content, status().isBadRequest()
    );

    assertThat(createPostCommentResponse.isSuccess()).isFalse();
    assertThat(createPostCommentResponse.getCode()).isEqualTo(
        ErrorCode.POST_ARCHIVED.getCode());
  }
}
