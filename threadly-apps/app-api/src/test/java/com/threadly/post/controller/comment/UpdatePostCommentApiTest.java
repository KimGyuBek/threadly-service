package com.threadly.post.controller.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.testsupport.fixture.posts.PostCommentFixtureLoader;
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
 * 게시글 댓글 상태 변경 관련 API 테스트
 * <p>
 * 테스트 데이터 = {/posts/comments/update-comment/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class UpdatePostCommentApiTest extends BasePostApiTest {

  @Autowired
  private PostCommentFixtureLoader postCommentFixtureLoader;


  @BeforeEach
  void setUp() throws Exception {
    postCommentFixtureLoader.load(
        "/posts/comments/update-comment/user.json",
        "/posts/comments/update-comment/post.json",
        "/posts/comments/update-comment/post-comment.json"
    );
  }

  // 게시글 ID
  public static final String ACTIVE_POST_ID = "active_post_id";

  // 댓글 상태별 댓글 ID
  public static final String ACTIVE_COMMENT_ID = "cmt_active_001";
  public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
  public static final String ARCHIVED_COMMENT_ID = "cmt_archive_001";
  public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

  // 댓글 작성자와 비작성자 이메일
  public static final String COMMENT_WRITER_EMAIL = "comment_writer@threadly.com";
  public static final String COMMENT_NOT_WRITER_EMAIL = "comment_not_writer@threadly.com";

  /**
   * deletePostComment() 테스트
   */

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 삭제 테스트")
  @Nested
  class deletePostCommentTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] deletePostComment - 댓글 작성자가 ACTIVE 상태 댁글 삭제 시 204 No Content 응답*/
      @DisplayName("1. 댓글 작성자가 ACTIVE 상태의 댓글 삭제시 성공 검증")
      @Test
      public void deletePostComment_shouldReturn204_whenCommentIsActive() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(COMMENT_WRITER_EMAIL);

        //when
        //then
        /*삭제 요청*/
        sendDeletePostCommentRequest(accessToken, ACTIVE_POST_ID,
            ACTIVE_COMMENT_ID, status().isNoContent());
      }

    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] deletePostComment - 댓글 작성자가 차단된 게시글에 댓글 삭제 요청 시 400 Bad Request 응답*/
      @Order(1)
      @DisplayName("1. 게시글 작성자가 BLOCKED 상태의 댓글 삭제 요청 시 400 Bad Request")
      @Test
      public void deletePostComment_shouldReturnBadRequest_whenPostIsBlocked() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(COMMENT_WRITER_EMAIL);

        //when
        //then
        /*삭제 요청*/
        CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
            ACTIVE_POST_ID,
            BLOCKED_COMMENT_ID, status().isBadRequest());

        assertThat(deletePostCommentResponse.isSuccess()).isFalse();
        assertThat(deletePostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_DELETE_BLOCKED.getCode());
      }

      /*[Case #2] deletePostComment - 댓글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden 응답 */
      @Order(2)
      @DisplayName("2. 댓글 작성자가 아닌 사용자가 삭제 요청 시 403 Forbidden")
      @Test
      public void deletePostComment_shouldReturnForbidden_whenCommentWriterNotEqualsUser()
          throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(COMMENT_NOT_WRITER_EMAIL);

        //when
        //then
        /*삭제 요청*/
        CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
            ACTIVE_POST_ID,
            ACTIVE_COMMENT_ID, status().isForbidden());

        assertThat(deletePostCommentResponse.isSuccess()).isFalse();
        assertThat(deletePostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_DELETE_FORBIDDEN.getCode());
      }

      /*[Case #3] deletePostComment -  이미 삭제된 댓글 삭제 요청 시 400 Bad Request 응답 */
      @Order(3)
      @DisplayName("3. 이미 삭제된 댓글 삭제 요청 시 400 Bad Request")
      @Test
      public void deletePostComment_shouldReturn400_whenCommentIsDeleted() throws Exception {
        //given

        /*로그인*/
        String accessToken = getAccessToken(COMMENT_WRITER_EMAIL);

        //when
        //then
        /*삭제 요청*/
        CommonResponse<Void> deletePostCommentResponse = sendDeletePostCommentRequest(accessToken,
            ACTIVE_POST_ID,
            DELETED_COMMENT_ID, status().isBadRequest());

        assertThat(deletePostCommentResponse.isSuccess()).isFalse();
        assertThat(deletePostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_ALREADY_DELETED.getCode());
      }
    }
  }


}
