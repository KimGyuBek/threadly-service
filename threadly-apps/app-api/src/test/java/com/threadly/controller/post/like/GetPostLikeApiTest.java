package com.threadly.controller.post.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.like.post.GetPostLikersApiResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 좋아요 조회 관련 API 테스트
 */
public class GetPostLikeApiTest extends BasePostApiTest {

  /**
   * getPostLikers - 테스트
   *
   * @throws Exception
   */
  /*[Case #1] getPostLikers - 게시글에 좋아요를 누른 사람이 있으면 그 목록을 조회해야한다*/
  @DisplayName("게시글 좋아요 목록 조회 - 좋아요가 존재하는 게시글에 요청을 보내면 좋아요 목록의 사이즈가 일치해야한다")
  @Test
  public void getPostLikers_shouldReturnLikers_whenPostHasLikers() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER);

    String postId = "post10";
    Long likeCount = POST_LIKES.get(postId);
    LocalDateTime cursorLikedAt = null;
    String cursorLikerId = null;
    int limit = 10;
    long size = 0;

    //when
    //then
    /*조회 요청*/
    while (true) {
      CommonResponse<GetPostLikersApiResponse> getPostLikersResponse = sendGetPostLikersRequest(
          accessToken, postId, cursorLikedAt, cursorLikerId, limit, status().isOk()
      );
      size += getPostLikersResponse.getData().postLikers().size();

      /*마지막 페이지 일 경우*/
      if (getPostLikersResponse.getData().cursorLikerId() == null
          || getPostLikersResponse.getData().cursorLikerId() == null) {
        break;
      }

      cursorLikedAt = getPostLikersResponse.getData().cursorLikedAt();
      cursorLikerId = getPostLikersResponse.getData().cursorLikerId();
    }
    assertThat(size).isEqualTo(likeCount);
  }

  /*[Case #2] getPostLikers - 존재하지 않는 게시글에 대해 좋아요 목록 요청 시 404 Not Found, 실패해야 한다*/
  @DisplayName("게시글 좋아요 목록 조회 - 존재하지 않는 게시글에 대해 요청 시 404 Not Found가 응답해야한다")
  @Test
  public void getPostLikers_shouldReturnNotFound_whenPostNotFound() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER);

    //when
    //then
    String postId = "post_not_exists";
    LocalDateTime cursorLikedAt = null;
    String cursorLikerId = null;
    int limit = 10;
    CommonResponse<GetPostLikersApiResponse> getPostLikersResponse = sendGetPostLikersRequest(
        accessToken, postId, cursorLikedAt, cursorLikerId, limit, status().isNotFound()
    );
    assertThat(getPostLikersResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
  }

  /*[Case #3] getPostLikers - 좋아요가 없는 게시글에 대해 좋아요 목록 요청 시 빈 리스트가 반환 되어야 한다*/
  @DisplayName("게시글 좋아요 목록 조회 - 좋아요가 없는 게시글에 요청 시 빈 리스트가 반환되어야 한다")
  @Test
  public void getPostLikers_shouldReturnEmptyList_whenPostHasNotLikes() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER);

    //when
    //then
    String postId = "post1";
    int likeCount = 0;
    LocalDateTime cursorLikedAt = null;
    String cursorLikerId = null;
    int limit = 10;
    int size = 0;

    //when
    //then
    /*조회 요청*/
    while (true) {
      CommonResponse<GetPostLikersApiResponse> getPostLikersResponse = sendGetPostLikersRequest(
          accessToken, postId, cursorLikedAt, cursorLikerId, limit, status().isOk()
      );
      size += getPostLikersResponse.getData().postLikers().size();

      /*마지막 페이지 일 경우*/
      if (getPostLikersResponse.getData().cursorLikerId() == null
          || getPostLikersResponse.getData().cursorLikerId() == null) {
        break;
      }

      cursorLikedAt = getPostLikersResponse.getData().cursorLikedAt();
      cursorLikerId = getPostLikersResponse.getData().cursorLikerId();
    }
    assertThat(size).isEqualTo(likeCount);
  }
}
