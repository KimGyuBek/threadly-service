package com.threadly.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
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
 * 게시글 상태 수정 관련 API 테스트
 *
 * 테스트 데이터 {/test/resources/fixtures/posts/post-delete//}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UpdatePostStatusApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  public static final String POST_OWNER_EMAIL = "sunset_gazer1@threadly.com";
  public static final String POST_NON_OWNER_EMAIL = "sky_gazer2@threadly.com";

  public static final String POST_ACTIVE_ID = "post_ACTIVE";
  public static final String POST_DELETED_ID = "post_DELETED";
  public static final String POST_BLOCKED_ID = "post_BLOCKED";
  public static final String POST_ARCHIVE_ID = "post_ARCHIVE";

  @BeforeEach
  void setUp() throws Exception {
    postFixtureLoader.load("/posts/post-delete/user.json", "/posts/post-delete/post.json");
  }

  /**
   * deletePost() - 게시글 삭제 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 삭제 테스트")
  @Nested
  class deletePostTest {

    @Order(1)
    @DisplayName("성공")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class success {

      /*[Case #1] deletePost() - 게시글 삭제 후 게시글 조회 및 목록 조회에서 해당 게시글이 포함 되지 않아야 함*/
      @DisplayName("1. 삭제 성공 후 게시글 및 목록 조회 시 해당 게시글이 포함되지 검증")
      @Test
      public void deletePost_shouldExcludePostFromPostList_afterSuccessfulDeletion()
          throws Exception {
        //given
        /*로그인 요청 전송*/
        String accessToken = getAccessToken(POST_OWNER_EMAIL);

        //when
        //then
        /*삭제 요청 전송*/
        CommonResponse<Void> deletePostResponse = sendDeletePostRequest(
            accessToken, POST_ACTIVE_ID, status().isOk());

        /*게시글 조회 요청 전송*/
        CommonResponse<GetPostDetailApiResponse> getPostResponse = sendGetPostRequest(
            accessToken, POST_ACTIVE_ID, status().isBadRequest());

        /*게시글 목록 조회 요청 전송*/
        CommonResponse<GetPostDetailListApiResponse> getPostListResponse = sendGetPostListRequest(
            accessToken, null, null, 10, status().isOk());

        assertThat(getPostResponse.isSuccess()).isFalse();
        assertThat(getPostResponse.getCode()).isEqualTo(ErrorCode.POST_ALREADY_DELETED.getCode());

        assertThat(getPostListResponse.getData().posts()).extracting(
                GetPostDetailApiResponse::postId)
            .doesNotContain(POST_ACTIVE_ID);
      }
    }

    @Order(2)
    @DisplayName("실패")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class fail {

      /*[Case #1] deletePost() - 삭제된 게시글에 삭제 요청시 400 BadRequest 반환해야 함*/
      @Order(1)
      @DisplayName("1. 이미 삭제된 게시글에 대해 삭제 요청 시 Bad Request")
      @Test
      public void deletePost_shouldReturnBadRequest_whenAlreadyDeleted() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(POST_OWNER_EMAIL);

        //when
        //then
        CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken,
            POST_DELETED_ID, status().isBadRequest());

        assertThat(deletePostResponse.isSuccess()).isFalse();
        assertThat(deletePostResponse.getCode()).isEqualTo(
            ErrorCode.POST_ALREADY_DELETED_ACTION.getCode());
      }

      /*[Case #2] deletePost() - 게시글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden, 실패해야한다.*/
      @Order(2)
      @DisplayName("2. 게시글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden")
      @Test
      public void deletePost_shouldReturnForbidden_whenNonOwnerRequest() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(POST_NON_OWNER_EMAIL);

        //when
        //then
        CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken,
            POST_DELETED_ID, status().isForbidden());

        assertThat(deletePostResponse.isSuccess()).isFalse();
        assertThat(deletePostResponse.getCode()).isEqualTo(
            ErrorCode.POST_DELETE_FORBIDDEN.getCode());
      }
    }
  }
}