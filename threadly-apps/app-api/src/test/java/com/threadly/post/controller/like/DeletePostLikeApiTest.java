package com.threadly.post.controller.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.core.port.post.in.like.post.LikePostApiResponse;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 게시글 좋아요 취소 관련 API 테스트
 * <p>
 * 테스트 데이터 {/test/resources/fixtures/posts/likes/like-post/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class DeletePostLikeApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postFixtureLoader.load("/posts/likes/like-post/user.json", "/posts/likes/like-post/post.json");
  }

  // 게시글 ID (status별 분류)
  public static final String ACTIVE_POST_ID = "active_post_id";
  public static final String ARCHIVED_POST_ID = "archived_post_id";
  public static final String BLOCKED_POST_ID = "blocked_post_id";
  public static final String DELETED_POST_ID = "deleted_post_id";

  // 게시글 작성자 이메일
  public static final String POST_WRITER_EMAIL = "writer@threadly.com";

  // 좋아요를 누를 사용자 이메일 목록
  public static final List<String> USERS = List.of(
      "sunset_gazer1@threadly.com",
      "sky_gazer2@threadly.com",
      "book_worm3@threadly.com",
      "beach_bum4@threadly.com",
      "early_bird5@threadly.com",
      "mountain_hiker6@threadly.com"
  );


  /**
   * cancelPostLike() 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 좋아요 취소 테스트")
  @Nested
  class cancelPostLikeTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] cancelPostLike - 좋아요를 누른 게시글에 좋아요 취소 요청을 보내면 좋아요가 취소되고 likeCount가 감소되어야 한다*/
      @Order(1)
      @DisplayName("1. 좋아요를 누른 게시글에 좋아요 취소 요청을 보내면 좋아요가 취소되고 likeCount가 감소되는지 검증")
      @Test
      public void cancelPostLike_shouldReturnCreated_whenUserLikedPost() throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        /*게시글 좋아요 요청*/
        CommonResponse<LikePostApiResponse> likePostResponse = sendLikePostRequest(
            accessToken,
            ACTIVE_POST_ID,
            status().isOk()
        );

        //when
        /*게시글 좋아요 취소 요청*/
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken,
            ACTIVE_POST_ID,
            status().isNoContent()
        );

        //then
        assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(
            likePostResponse.getData().likeCount() - 1);
      }

      /*[Case #2] cancelPostLike - 좋아요를 누른 게시글에 좋아요 취소 요청을 여러번 보내도 멱등해야 한다 */
      @Order(2)
      @DisplayName("2. 사용자가 좋아요를 누른 게시글에 좋아요 취소 요청을 여러번 보내도 멱등하는지 검증")
      @Test
      public void cancelPostLike_shouldBeIdempotent_whenUserMultipleRequest() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        /*게시글 좋아요 요청*/
        CommonResponse<LikePostApiResponse> likePostRequest = sendLikePostRequest(
            accessToken, ACTIVE_POST_ID, status().isOk());

        //when
        //then
        /*좋아요 요청 여러번 전송*/
        for (int i = 0; i < 3; i++) {
          assertThat(sendCancelPostLikeRequest(accessToken, ACTIVE_POST_ID,
              status().isNoContent()).isSuccess()).isTrue();
        }
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken, ACTIVE_POST_ID, status().isNoContent());
        assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(
            likePostRequest.getData().likeCount() - 1);
      }

      /*[Case #3] cancelPostLike - 좋아요를 누르지 않은 게시글에 좋아요 취소 요청을 보내면 멱등해야 한다 */
      @Order(3)
      @DisplayName("3. 사용자가 좋아요를 누르지 않은 게시글에 좋아요 취소 요청을 보내면 멱등하는지 검증")
      @Test
      public void cancelPostLike_shouldIdempotent_whenUserLikedPostMultipleRequest()
          throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*좋아요 요청 여러번 전송*/
        for (int i = 0; i < 3; i++) {
          assertThat(sendCancelPostLikeRequest(accessToken, ACTIVE_POST_ID,
              status().isNoContent()).isSuccess()).isTrue();
        }
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken, ACTIVE_POST_ID, status().isNoContent());
        assertThat(cancelPostLikeRequest.getData().likeCount()).isEqualTo(0);

      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {
      /*[Case #1] cancelPostLike - DELETED 상태의 게시글에 좋아요 취소 요청을 보내면 400 Bad Request, 실패해야한다*/
      @Order(1)
      @DisplayName("1. DELETED 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest")
      @Test
      public void cancelPostLike_shouldReturnBadRequest_whenPostDeleted() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken, DELETED_POST_ID, status().isBadRequest());

        assertThat(cancelPostLikeRequest.getCode()).isEqualTo(
            ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }

      /*[Case #2] cancelPostLike - BLOCKED 상태의 게시글에 좋아요 취소 요청을 보내면 400 Bad Request, 실패해야한다*/
      @Order(2)
      @DisplayName("2. BLOCKED 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest")
      @Test
      public void cancelPostLike_shouldReturnBadRequest_whenPostBlocked() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken, BLOCKED_POST_ID, status().isBadRequest());

        assertThat(cancelPostLikeRequest.getCode()).isEqualTo(
            ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }

      /*[Case #3] cancelPostLike - ARCHIVE 상태의 게시글에 좋아요 취소 요청을 보내면 400 Bad Request, 실패해야한다*/
      @Order(3)
      @DisplayName("3. ARCHIVE 상태의 게시글에 좋아요 취소 요청을 보내면 BadRequest")
      @Test
      public void cancelPostLike_shouldReturnBadRequest_whenPostArchive() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostApiResponse> cancelPostLikeRequest = sendCancelPostLikeRequest(
            accessToken, ARCHIVED_POST_ID, status().isBadRequest());

        assertThat(cancelPostLikeRequest.getCode()).isEqualTo(
            ErrorCode.POST_LIKE_NOT_ALLOWED.getCode());
      }

    }
  }




}
