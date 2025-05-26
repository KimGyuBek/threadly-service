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
 * 게시글 좋아요 생성 관련 API 테스트
 */
public class LikePostApiTest extends BasePostApiTest {

  /**
   * likePost() 테스트
   *
   * @throws Exception
   */
  /*[Case #1] likePost - ACTIVE 상태의 게시글에 좋아요를 눌렀을 경우 성공해야한다*/
  @DisplayName("게시글 좋아요 생성 - 정상 요청시 201 Created, likeCount=1을 반환한다")
  @Test
  public void likePost_shouldReturnCreated_whenUserLikesActivePost() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = ACTIVE_POSTS.get(0).get("postId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*게시글 좋아요 요청 전송*/
    CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
        accessToken,
        postId,
        status().isOk()
    );
    assertThat(likePostResponse.getData().likeCount()).isEqualTo(1);
  }

  /*[Case #2] likePost - 동일 사용자가 같은 게시글에 여러번 좋아요 요청을 해도 멱등하게 처리되어야 한다*/
  @DisplayName("게시글 좋아요 - 동일 사용자가 중복 좋아요 요청 시 멱등하게 처리됨")
  @Test
  public void likePost_shouldBeIdempotent_whenUserLikesSamePostMultipleTimes() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = ACTIVE_POSTS.get(0).get("postId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*게시글 좋아요 요청 전송*/
    for (int i = 0; i < 3; i++) {
      assertThat(
          sendLikePostRequest(accessToken, postId, status().isOk()).isSuccess()).isTrue();
    }
    CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
        accessToken,
        postId,
        status().isOk()
    );
    assertThat(likePostResponse.getData().likeCount()).isEqualTo(1);
  }

  /*[Case #3] likePost - ACTIVE 상태가 아닌 게시글에 좋아요를 누를경우 400 Bad Request, 실패해야한다 */
  @DisplayName("게시글 좋아요 - 비활성 상태의 ")
  @Test
  public void likePost_shouldReturnBadRequest_whenPostNotActive() throws Exception {
    //given
    String email = VERIFIED_USER_EMAILS.get(0);
    String postId = DELETED_POSTS.get(0).get("postId");

    /*로그인*/
    String accessToken = getAccessToken(email);

    //when
    //then
    /*게시글 좋아요 요청 전송*/
    CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
        accessToken,
        postId,
        status().isBadRequest()
    );
    assertThat(likePostResponse.getCode()).isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
  }

  /*[Case #4] likePost - 여러 사용자가 댓글에 좋아요를 누를 경우 likeCount가 누적되어 증가해야 한다*/
  @DisplayName("게시글 좋아요 생성 - 여러 사용자가 게시글에 좋아요를 누를 경우 likeCount 누적되어야 한다")
  @Test
  public void likePost_shouldAccumulateLikeCount_whenMultipleUserLike() throws Exception {
    //given
    String postId = ACTIVE_POSTS.get(0).get("postId");

    //when
    //then
    /*게시글 좋아요 요청 전송*/
    for (int i = 1; i < VERIFIED_USER_EMAILS.size(); i++) {
      assertThat(sendLikePostRequest(
          getAccessToken(VERIFIED_USER_EMAILS.get(i)), postId, status().isOk()
      ).isSuccess()).isTrue();
    }
    CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
        getAccessToken(VERIFIED_USER_EMAILS.getFirst()),
        postId,
        status().isOk()
    );
    assertThat(likePostResponse.getData().likeCount()).isEqualTo(VERIFIED_USER_EMAILS.size());
  }
}
