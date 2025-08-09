package com.threadly.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.usecase.post.create.CreatePostApiResponse;
import com.threadly.post.image.BasePostImageApiTest;
import com.threadly.core.usecase.post.image.UploadPostImagesApiResponse;
import com.threadly.testsupport.fixture.posts.PostCommentLikeFixtureLoader;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import com.threadly.testsupport.fixture.posts.PostLikeFixtureLoader;
import com.threadly.utils.TestConstants;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.springframework.mock.web.MockMultipartFile;

/**
 * 게시글 삭제 관련 API 테스트
 */
@DisplayName("게시글 삭제 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class DeletePostApiTest extends BasePostImageApiTest {

  @Autowired
  private PostLikeFixtureLoader postLikeFixtureLoader;

  @Autowired
  private PostCommentLikeFixtureLoader postCommentLikeFixtureLoader;

  @Autowired
  private PostFixtureLoader postFixtureLoader;


  @BeforeEach
  void setUp() throws IOException {
    super.clearFiles();

    postCommentLikeFixtureLoader.load(
        "/posts/delete/user.json",
        "/posts/delete/post.json",
        "/posts/delete/post-comment.json",
        "/posts/delete/comment-like.json"
    );
    postLikeFixtureLoader.load(
        "/posts/delete/post-like.json"
    );
  }

  // 게시글 정보
  public static final String POST_ACTIVE_ID = "post1";
  public static final String POST_OWNER_EMAIL = "author@threadly.com";

  // 게시글 좋아요 수
  public static final int POST_LIKE_COUNT = 10;

  // 댓글 정보
  public static final int POST_COMMENT_COUNT = 5;
  public static final Map<String, Integer> COMMENT_LIKE_COUNT = Map.of(
      "comment_1", 3,
      "comment_3", 1
  );

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 삭제 검증 - 이미지를 포함한 게시글 삭제 요청 시 DELETED 상태 검증*/
    @Order(1)
    @DisplayName("1. 이미지가 있는 게시글 삭제 요청 시 DELETED 상태 검증")
    @Test
    public void deletePostWithImage_shouldSucceed_whenHasImage() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      /*이미지 업로드*/
      List<MockMultipartFile> images = generateUploadImagesWithRatio(1, 300, 400);

      CommonResponse<UploadPostImagesApiResponse> uploadImageResponse = sendUploadPostImage(
          accessToken, images, status().isCreated()
      );

      /*게시글 생성*/
      CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostWithImages(
          accessToken, uploadImageResponse.getData().images());

      String postId = createPostResponse.getData().postId();

      //when
      /*게시글 삭제 */
      sendDeletePostRequest(accessToken, postId, status().isOk());

      //then
      /*검증*/
      validateImageResponse(createPostResponse, postId, ImageStatus.DELETED);
      validatePostStatus(postId, PostStatus.DELETED);
    }

    /*[Case #2] 게시글 삭제 검증 - 이미지가 없는 게시글 삭제 요청 시 DELETED 상태 검증*/
    @Order(2)
    @DisplayName("2. 이미지가 없는 게시글 삭제 요청 시 DELETED 상태 검증")
    @Test
    public void deletePostWithImage_shouldSucceed_whenHasNotImage() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(POST_OWNER_EMAIL);

      //when
      /*게시글 삭제 */
      sendDeletePostRequest(accessToken, POST_ACTIVE_ID, status().isOk());

      //then
      /*검증*/
      validatePostStatus(POST_ACTIVE_ID, PostStatus.DELETED);
    }

    /*[Case #3] 게시글 삭제 검증 - 게시글 좋아요, 댓글, 댓글 좋아요가 있는 게시글 삭제 요청 시 데이터 검증*/
    @Order(3)
    @DisplayName("3. 모든 활동이 있는 게시글을에 대한 삭제 요청 시 데이터 검증")
    @Test
    public void deletePostWithImage_shouldSucceed_whenHasAllActivities() throws Exception {
      //given
      /*게시글 작성자 로그인*/
      String accessToken = getAccessToken(POST_OWNER_EMAIL);

      //when
      /*게시글 삭제 */
      sendDeletePostRequest(accessToken, POST_ACTIVE_ID, status().isOk());

      //then
      /*게시글 상태 검증*/
      validatePostStatus(POST_ACTIVE_ID, PostStatus.DELETED);

      /*게시글 좋아요 삭제 검증*/
      validatePostLike(POST_ACTIVE_ID, 0);

      /*게시글 댓글 삭제 검증*/
      validateCommentCountByStatusAndPostId(POST_ACTIVE_ID, PostCommentStatus.DELETED,
          POST_COMMENT_COUNT);

      /*댓글 좋아요 삭제 검증*/
      validateCommentLikeCountByPostId(POST_ACTIVE_ID, 0);
    }
  }


  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    @BeforeEach
    void setUp() throws IOException {
      /*게시글 테스트 데이터 */
      postFixtureLoader.load("/posts/post-delete/user.json", "/posts/post-delete/post.json");
    }

    public static final String POST_OWNER_EMAIL = "sunset_gazer1@threadly.com";
    public static final String POST_NON_OWNER_EMAIL = "sky_gazer2@threadly.com";

    public static final String POST_ACTIVE_ID = "post_ACTIVE";
    public static final String POST_DELETED_ID = "post_DELETED";
    public static final String POST_BLOCKED_ID = "post_BLOCKED";

    /*[Case #1] 게시글 삭제 검증 - 존재하지 않는 postId에 대해서 삭제 요청 시 404 Not Found*/
    @Order(1)
    @DisplayName("1. 존재하지 않는 postId로 삭제 요청 시 404 Not Found")
    @Test
    public void deletePostWithImage_shouldReturnNotFound_whenPostIdNotFound() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      String postId = "postId";

      //when
      /*게시글 삭제 */
      CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken, postId,
          status().isNotFound());

      //then
      /*검증*/
      assertThat(deletePostResponse.isSuccess()).isFalse();
      assertThat(deletePostResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
    }

    /*[Case #2] 게시글 삭제 검증 - 이미 삭제 된 게시글을 삭제 요청 시 400 Bad Request*/
    @Order(2)
    @DisplayName("2. 이미 삭제된 게시글에 대해 삭제 요청 시 400 Bad Request")
    @Test
    public void deletePostWithImage_shouldReturnBadRequest_whenPostAlreadyDeleted()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(POST_OWNER_EMAIL);

      //when
      /*게시글 삭제 */
      CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken, POST_DELETED_ID,
          status().isBadRequest());

      //then
      /*검증*/
      assertThat(deletePostResponse.isSuccess()).isFalse();
      assertThat(deletePostResponse.getCode()).isEqualTo(
          ErrorCode.POST_ALREADY_DELETED_ACTION.getCode());
    }

    /*[Case #3] 게시글 삭제 검증 - 차단된 게시글을 삭제 요청 시 400 Bad Request*/
    @Order(3)
    @DisplayName("3. 차단된 게시글을 삭제 요청 시 400 Bad Request")
    @Test
    public void deletePostWithImage_shouldReturnBadRequest_whenPostBlocked()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(POST_OWNER_EMAIL);

      //when
      /*게시글 삭제 */
      CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken, POST_BLOCKED_ID,
          status().isBadRequest());

      //then
      /*검증*/
      assertThat(deletePostResponse.isSuccess()).isFalse();
      assertThat(deletePostResponse.getCode()).isEqualTo(
          ErrorCode.POST_DELETE_BLOCKED.getCode());
    }

    /*[Case #4] 게시글 삭제 검증 - 게시글 작성자와 삭제 요청자가 일치하지 않는 경우 403 Forbidden*/
    @Order(4)
    @DisplayName("4. 게시글 작성자와 삭제 요청자가 일치 하지 않는 경우 403 Forbidden")
    @Test
    public void deletePostWithImage_shouldReturnForBidden_whenPostWriterNotEqualsRequester()
        throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(POST_NON_OWNER_EMAIL);

      //when
      /*게시글 삭제 */
      CommonResponse<Void> deletePostResponse = sendDeletePostRequest(accessToken, POST_ACTIVE_ID,
          status().isForbidden());

      //then
      /*검증*/
      assertThat(deletePostResponse.isSuccess()).isFalse();
      assertThat(deletePostResponse.getCode()).isEqualTo(
          ErrorCode.POST_DELETE_FORBIDDEN.getCode());
    }

  }


}
