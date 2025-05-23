package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 상태 수정 관련 API 테스트
 */
class UpdatePostStatusApiTest extends BasePostApiTest {

  /**
   * deletePost() - 게시글 삭제 테스트
   */
  /*[Case #1] deletePost() - 게시글 삭제 후 게시글 조회 및 목록 조회에서 해당 게시글이 포함 되지 않아야 함*/
  @DisplayName("게시글 삭제 - 삭제 성공 후 게시글 및 목록 조회 시 해당 게시글이 포함되지 않아야 한다")
  @Test
  public void deletePost_shouldExcludePostFromPostList_afterSuccessfulDeletion() throws Exception {
    //given
    String email = ACTIVE_POSTS.get(1).get("userEmail");
    String postId = ACTIVE_POSTS.get(1).get("postId");

    /*로그인 요청 전송*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*삭제 요청 전송*/
    CommonResponse<Void> deletePostResponse = sendDeletePostRequest(
        accessToken, postId, status().isOk());

    /*게시글 조회 요청 전송*/
    CommonResponse<PostDetailApiResponse> getPostResponse = sendGetPostRequest(
        accessToken, postId, status().isBadRequest());

    /*게시글 목록 조회 요청 전송*/
    CommonResponse<PostDetailListApiResponse> getPostListResponse = sendGetPostListRequest(
        accessToken, null, null, 10, status().isOk());

    assertThat(getPostResponse.isSuccess()).isFalse();
    assertThat(getPostResponse.getCode()).isEqualTo(ErrorCode.POST_ALREADY_DELETED.getCode());

    assertThat(getPostListResponse.getData().posts()).extracting(PostDetailApiResponse::postId)
        .doesNotContain(postId);
  }

  /*[Case #2] deletePost() - 삭제된 게시글에 삭제 요청시 400 BadRequest 반환해야 함*/
  @DisplayName("게시글 삭제 - 이미 삭제된 게시글에 대해 삭제 요청을 보내면 Bad Request 오류가 나야 한다")
  @Test
  public void deletePost_shouldReturnBadRequest_whenAlreadyDeleted() throws Exception {
    //given
    String email = DELETED_POSTS.get(1).get("userEmail");
    String postId = DELETED_POSTS.get(1).get("postId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken,
        postId, status().isBadRequest());

    assertThat(deletePostResponse.isSuccess()).isFalse();
    assertThat(deletePostResponse.getCode()).isEqualTo(
        ErrorCode.POST_ALREADY_DELETED_ACTION.getCode());
  }


}