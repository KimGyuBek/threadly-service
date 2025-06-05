package com.threadly.post.controller.comment;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 게시글 댓글 생성 관련 API 테스트
 * <p>
 * 테스트 데이터 : {/posts/comments/create-comment}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class CreatePostCommentApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    super.setUpDefaultUser();
    postFixtureLoader.load(
        "/posts/comments/create-comment/user.json",
        "/posts/comments/create-comment/post.json"
    );
  }

  // 게시글 ID (status별 분류)
  public static final String ACTIVE_POST_ID = "active_post_id";
  public static final String ARCHIVED_POST_ID = "archived_post_id";
  public static final String BLOCKED_POST_ID = "blocked_post_id";
  public static final String DELETED_POST_ID = "deleted_post_id";


  /**
   * createPostcomment() 테스트
   */
  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 생성 테스트")
  @Nested
  class createPostCommentTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] createPostComment()- ACTIVE 상태인 게시글에 댓글을 작성할 경우 성공해야 한다*/
      @DisplayName("1. ACTIVE 상태의 게시글에 댓글 작성 요청 하는 경우")
      @Test
      public void createPostComment_shouldSucceed_whenPostIsActive() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        String content = "게시글 댓글 생성";

        /*댓글 추가 요청 전송*/
        CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
            accessToken, ACTIVE_POST_ID, content, status().isCreated()
        );

        assertThat(createPostCommentResponse.getData().content()).isEqualTo(content);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] createPostComment()- DELETED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
      @Order(1)
      @DisplayName("1. DELETED 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request")
      @Test
      public void createPostComment_shouldReturnBadRequest_whenPostIsDeleted() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        String content = "게시글 댓글 생성";

        /*댓글 추가 요청 전송*/
        CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
            accessToken, DELETED_POST_ID, content, status().isBadRequest()
        );

        assertThat(createPostCommentResponse.isSuccess()).isFalse();
        assertThat(createPostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_ALREADY_DELETED.getCode());
      }

      /*[Case #2] createPostComment()- BLOCKED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
      @Order(2)
      @DisplayName("2. BLOCKED 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request")
      @Test
      public void createPostComment_shouldReturnBadRequest_whenPostIsBlocked() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        String content = "게시글 댓글 생성";

        /*댓글 추가 요청 전송*/
        CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
            accessToken, BLOCKED_POST_ID, content, status().isBadRequest()
        );

        assertThat(createPostCommentResponse.isSuccess()).isFalse();
        assertThat(createPostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_BLOCKED.getCode());
      }

      /*[Case #3]  createPostComment() - ARCHIVED 상태인 게시글에 댓글을 작성할 경우 Bad Request를 응답해야 한다*/
      @Order(3)
      @DisplayName("3. ARCHIVE 상태의 게시글에 댓글 작성 요청을 하는 경우 400 Bad Request")
      @Test
      public void createPostComment_shouldReturnBadRequest_whenPostIsArchived() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        String content = "게시글 댓글 생성";

        /*댓글 추가 요청 전송*/
        CommonResponse<CreatePostCommentApiResponse> createPostCommentResponse = sendCreatePostCommentRequest(
            accessToken, ARCHIVED_POST_ID, content, status().isBadRequest()
        );

        assertThat(createPostCommentResponse.isSuccess()).isFalse();
        assertThat(createPostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_ARCHIVED.getCode());
      }
    }
  }


}
