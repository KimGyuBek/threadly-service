package com.threadly.controller.post.comment;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import com.threadly.testsupport.fixture.posts.PostCommentFixtureLoader;
import java.time.LocalDateTime;
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
 * 게시글 댓글 조회 관련 API 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetPostCommentApiTest extends BasePostApiTest {

  @Autowired
  private PostCommentFixtureLoader postCommentFixtureLoader;

  @BeforeEach
  void setup() {
    super.setUpDefaultUser();
    postCommentFixtureLoader.load(
        "/posts/comments/get-comment/user.json",
        "/posts/comments/get-comment/post.json",
        "/posts/comments/get-comment/post-comment.json"
    );


  }

  public static final String ACTIVE_POST_ID = "active_post_id";
  public static final String ARCHIVED_POST_ID = "archived_post_id";
  public static final String BLOCKED_POST_ID = "blocked_post_id";
  public static final String DELETED_POST_ID = "deleted_post_id";

  public static final int ACTIVE_POST_COMMENT_COUNT = 101;

  /**
   * 게시글 댓글 목록 커서 기반 조회
   * <p>
   * 테스트 데이터 = {/posts/comments/get-comment/}
   */

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 목록 커서 기반 조회")
  @Nested
  class getPostCommentListTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] getPostCommentList - 첫 페이지 응답에서 커서가 정상적으로 설정되어야 한다*/
      @Order(1)
      @DisplayName("1. 게시글 댓글 목록 첫 페이지 조회 시 응답에 커서가 정상적으로 조회되는지 검증")
      @Test
      public void getPostCommentList_shouldReturnNextCursor_whenFirstPageRequested()
          throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken, ACTIVE_POST_ID, cursorCommentedAt, cursorCommentId, limit,
            status().isOk()
        );
        assertThat(getPostCommentListResponse.getData().nextCursor().cursorCommentedAt()).isEqualTo(
            getPostCommentListResponse.getData().nextCursor().cursorCommentedAt());
        assertThat(getPostCommentListResponse.getData().nextCursor().cursorCommentId()).isEqualTo(
            getPostCommentListResponse.getData().nextCursor().cursorCommentId());
      }

      /*[Case #2] getPostCommentList - 게시글 댓글 목록 전체 조회 검증*/
      @Order(2)
      @DisplayName("2. 게시글 댓글 목록 전체 조회 검증")
      @Test
      public void getPostCommentList_shouldReturnAllComments() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;
        int size = 0;

        while (true) {
          CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
              accessToken, ACTIVE_POST_ID, cursorCommentedAt, cursorCommentId, limit,
              status().isOk()
          );

          /*사이즈 증가*/
          size += getPostCommentListResponse.getData().comments().size();


          /*마지막 페이지일경우 종료*/
          if (getPostCommentListResponse.getData().nextCursor().cursorCommentId() == null) {
            break;
          }

          /*커서 검증*/
          assertThat(
              getPostCommentListResponse.getData().comments().getLast().commentId()).isEqualTo(
              getPostCommentListResponse.getData().nextCursor().cursorCommentId());
          assertThat(
              getPostCommentListResponse.getData().comments().getLast().commentedAt()).isEqualTo(
              getPostCommentListResponse.getData().nextCursor().cursorCommentedAt());

          /*커서 지정*/
          cursorCommentId = getPostCommentListResponse.getData().nextCursor().cursorCommentId();
          cursorCommentedAt = getPostCommentListResponse.getData().nextCursor().cursorCommentedAt();
        }
        assertThat(size).isEqualTo(ACTIVE_POST_COMMENT_COUNT);
      }

      /*[Case #3] getPostCommentList - 댓글 좋아요 요청 후 댓글 목록 조회 시 liked 필드가 true로 반영되어야 한다*/
      @Order(3)
      @DisplayName("3. 게시글 댓글 좋아요 요청 후 댓글 목록 조회 시 해당 댓글의 liked 필드가 true로 변하는지 검증")
      @Test
      public void getPostCommentList_shouldReflectLikedField_whenLikeRequest() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        /*게시글 댓글 목록 조회*/
        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse1 = sendGetPostCommentListRequest(
            accessToken, ACTIVE_POST_ID, cursorCommentedAt, cursorCommentId, limit, status().isOk()
        );

        String commentId = getPostCommentListResponse1.getData().comments().getFirst().commentId();

        /*좋아요 요청*/
        sendLikePostCommentRequest(
            accessToken, ACTIVE_POST_ID, commentId, status().isOk()
        );

        /*게시글 댓글 목록 조회*/
        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse2 = sendGetPostCommentListRequest(
            accessToken, ACTIVE_POST_ID, cursorCommentedAt, cursorCommentId, limit, status().isOk()
        );

        assertThat(
            getPostCommentListResponse2.getData().comments().getFirst().liked()).isNotEqualTo(
            getPostCommentListResponse1.getData().comments().getFirst().liked());
        assertThat(getPostCommentListResponse2.getData().comments().getFirst().liked()).isTrue();
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] getPostCommentList - DELETED 상태의 게시글 댓글 목록 요청 시 400 Bad Request, 실패해야 한다*/
      @Order(1)
      @DisplayName("1. DELETED 상태의 게시글 댓글 목록 조회 시 400 Bad Request")
      @Test
      public void getPostCommentList_shouldReturnBadRequest_whenPostIsDeleted() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken, DELETED_POST_ID, cursorCommentedAt, cursorCommentId, limit,
            status().isBadRequest()
        );

        assertThat(getPostCommentListResponse.isSuccess()).isFalse();
        assertThat(getPostCommentListResponse.getCode()).isEqualTo(
            ErrorCode.POST_NOT_ACCESSIBLE.getCode());
      }

      /*[Case #2] getPostCommentList - ARCHIVE 상태의 게시글 댓글 목록 요청 시 400 Bad Request, 실패해야 한다*/
      @Order(2)
      @DisplayName("2. ARCHIVE 상태의 게시글 댓글 목록 조회 시 400 Bad Request")
      @Test
      public void getPostCommentList_shouldReturnBadRequest_whenPostIsArchive() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken, ARCHIVED_POST_ID, cursorCommentedAt, cursorCommentId, limit,
            status().isBadRequest()
        );

        assertThat(getPostCommentListResponse.isSuccess()).isFalse();
        assertThat(getPostCommentListResponse.getCode()).isEqualTo(
            ErrorCode.POST_NOT_ACCESSIBLE.getCode());
      }

      /*[Case #3] getPostCommentList - BLOCKED 상태의 게시글 댓글 목록 요청 시 400 Bad Request, 실패해야 한다*/
      @Order(3)
      @DisplayName("3. BLOCKED 상태의 게시글 댓글 목록 조회 시 400 Bad Request")
      @Test
      public void getPostCommentList_shouldReturnBadRequest_whenPostIsBlocked() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken, DELETED_POST_ID, cursorCommentedAt, cursorCommentId, limit,
            status().isBadRequest()
        );

        assertThat(getPostCommentListResponse.isSuccess()).isFalse();
        assertThat(getPostCommentListResponse.getCode()).isEqualTo(
            ErrorCode.POST_NOT_ACCESSIBLE.getCode());
      }

      /*[Case #4] getPostCommentList - 존재하지 않는 게시글에 대한 댓글 목록을 요청 시 404 Not Found, 실패해야한다*/
      @DisplayName("4. 존재하지 않는 게시글의 댓글 목록 요청 시 400 Not Found")
      @Test
      public void getPostCommentList_shouldReturnBadRequest_whenPostNotExists() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String postId = "post_not_exists_id";

        //when
        //then
        LocalDateTime cursorCommentedAt = null;
        String cursorCommentId = null;
        int limit = 10;

        CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken, postId, cursorCommentedAt, cursorCommentId, limit, status().isNotFound()
        );

        assertThat(getPostCommentListResponse.isSuccess()).isFalse();
        assertThat(getPostCommentListResponse.getCode()).isEqualTo(
            ErrorCode.POST_NOT_FOUND.getCode());
      }
    }
  }
}

