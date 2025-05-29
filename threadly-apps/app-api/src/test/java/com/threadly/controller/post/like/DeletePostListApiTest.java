package com.threadly.controller.post.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.like.post.LikePostApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 좋아요 취소 관련 API 테스트
 */
public class DeletePostListApiTest extends BasePostApiTest {

  /**
   * cancelPostLike() 테스트
   */
  /*[Case #1] cancelPostLike - 좋아요를 누른 게시글에 좋아요 취소 요청을 보내면 좋아요가 취소되고 likeCount가 감소되어야 한다*/
  @DisplayName("게시글 좋아요 취소 - 좋아요를 누른 게시글에 좋아요 취소 요청을 보내면 좋아요가 취소되고 likeCount가 감소되어야 한다")
  @Test
  public void cancelPostLike_shouldReturnCreated_whenUserLikedPost() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = ACTIVE_POSTS.get(0).get("postId");

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    /*게시글 좋아요 요청*/
    CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
        accessToken,
        postId,
        status().isOk()
    );

    //when
    /*게시글 좋아요 취소 요청*/
    CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
        accessToken,
        postId,
        status().isNoContent()
    );

    //then

    assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(
        likePostResponse.getData().likeCount() - 1);
  }

  /*[Case #2] cancelPostLike - 좋아요를 누른 게시글에 좋아요 취소 요청을 여러번 보내도 멱등해야 한다 */
  @DisplayName("게시글 좋아요 취소 - 사용자가 좋아요를 누른 게시글에 좋아요 취소 요청을 여러번 보내도 멱등해야 한다")
  @Test
  public void cancelPostLike_shouldBeIdempotent_whenUserMultipleRequest() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = ACTIVE_POSTS.get(0).get("postId");

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    /*게시글 좋아요 요청*/
    CommonResponse<LikePostApiResponse> likePostRequest = sendLikePostRequest(
        accessToken, postId, status().isOk());

    //when
    //then
    /*좋아요 요청 여러번 전송*/
    for (int i = 0; i < 3; i++) {
      assertThat(sendCancelPostLikeRequest(accessToken, postId,
          status().isNoContent()).isSuccess()).isTrue();
    }
    CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
        accessToken, postId, status().isNoContent());
    assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(
        likePostRequest.getData().likeCount() - 1);
  }

  /*[Case #3] cancelPostLike - 좋아요를 누르지 않은 게시글에 좋아요 취소 요청을 보내면 멱등해야 한다 */
  @DisplayName("게시글 좋아요 취소 - 사용자가 좋아요를 누르지 않은 게시글에 좋아요 취소 요청을 보내면 멱등해야 한다")
  @Test
  public void cancelPostLike_shouldIdempotent_whenUserLikedPostMultipleRequest() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = ACTIVE_POSTS.get(0).get("postId");

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*좋아요 요청 여러번 전송*/
    for (int i = 0; i < 3; i++) {
      assertThat(sendCancelPostLikeRequest(accessToken, postId,
          status().isNoContent()).isSuccess()).isTrue();
    }
    CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
        accessToken, postId, status().isNoContent());
    assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(0);

  }

  /*[Case #4] cancelPostLike - 비활성 상태의 게시글에 좋아요 취소 요청을 보내면 400 Bad Request, 실패해야한다*/
  @DisplayName("게시글 좋아요 취소 - 비활성 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest, 실패해야한다")
  @Test
  public void cancelPostLike_shouldReturnBadRequest_whenPostNotActive() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = DELETED_POSTS.get(0).get("postId");

    /*로그인 요청*/
    String accessToken = getAccessToken(email);

    //when
    //then
    CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
        accessToken, postId, status().isBadRequest());

    assertThat(cancelPostLikeRequest.getCode()).isEqualTo(
        ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
  }


}
