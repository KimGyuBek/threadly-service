package com.threadly.post.controller.comment.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentApiResponse;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.core.port.post.in.like.comment.query.dto.PostCommentLiker;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.testsupport.fixture.posts.PostCommentLikeFixtureLoader;
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
 * 게시글 댓글 좋아요 조회 관련 API 테스트
 * <p>
 * 테스트 데이터 = { "/posts/comments/likes/get-comment-like/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetPostCommentLikeApiTest extends BasePostApiTest {

  @Autowired
  private PostCommentLikeFixtureLoader postCommentLikeFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postCommentLikeFixtureLoader.load(
        "/posts/comments/likes/get-comment-like/user.json",
        "/posts/comments/likes/get-comment-like/post.json",
        "/posts/comments/likes/get-comment-like/post-comment.json",
        "/posts/comments/likes/get-comment-like/post-comment-like.json"
    );
  }

  // 게시글 ID
  public static final String ACTIVE_POST_ID = "active_post_id";

  // 댓글 ID (상태별)
  //좋아요가 있는 댓글
  public static final String ACTIVE_COMMENT_WITH_LIKES_ID = "cmt_active_001";

  //좋아요가 없는 댓글
  public static final String ACTIVE_COMMENT_WITHOUT_LIKES_ID = "cmt_active_002";

  public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
  public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

  // 전체 좋아요 사용자 수
  public static final int COMMENT_LIKE_USER_COUNT = 101;

  /**
   * getPostLikers() 테스트
   *
   * @throws Exception
   */
  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 좋아요 목록 조회 테스트")
  @Nested
  class getPostLikersTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] getPostLikers - 좋아요가 있는 댓글의 목록을 조회할 경우 첫 페이지 응답에서 커서가 정상적으로 설정되어야한다*/
      @Order(1)
      @DisplayName("1. 좋아요가 있는 댓글의 좋아요 목록 조회 요청 시 응답 커서 검증")
      @Test
      public void getPostCommentLikers_shouldReturnFirstPage() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        /*게시글 댓글 목록 조회*/
        CommonResponse<CursorPageApiResponse<GetPostCommentApiResponse>> getPostCommentListResponse = sendGetPostCommentListRequest(
            accessToken,
            ACTIVE_POST_ID,
            null,
            null,
            10,
            status().isOk()
        );

        //when
        //then
        /*게시글 댓글 좋아요 목록 조회 요청*/
        CommonResponse<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikerListResponse = sendGetPostCommentLikersRequest(
            accessToken,
            ACTIVE_POST_ID,
            ACTIVE_COMMENT_WITH_LIKES_ID,
            null,
            null, 10, status().isOk()
        );

        //    커서 검증
        assertThat(
            getPostCommentLikerListResponse.getData().content().getLast().likedAt()).isEqualTo(
            getPostCommentLikerListResponse.getData().nextCursor().cursorTimestamp());
        assertThat(
            getPostCommentLikerListResponse.getData().content().getLast().liker()
                .userId()).isEqualTo(
            getPostCommentLikerListResponse.getData().nextCursor().cursorId());
      }

      /*[Case #2] getPostLikers - 좋아요가 없는 댓글에 대한 요청 시 빈 리스트가 반환되어야한다.*/
      @Order(2)
      @DisplayName("2. 좋아요가 없는 댓글에 요청 시 빈 리스트가 반환되는지 검증")
      @Test
      public void getPostLikers_shouldReturnEmptyList_whenPostCommentHasNotLikes()
          throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
            accessToken,
            ACTIVE_POST_ID, ACTIVE_COMMENT_WITHOUT_LIKES_ID, null, null, 10, status().isOk()
        );

        assertThat(getPostCommentLikersResponse.getData().content()).isEmpty();
      }

      /*[Case #3] getPostLikers - 전체 좋아요 목록 조회 시 마지막 페이지까지 순회 조화한다*/
      @Order(3)
      @DisplayName("3. 전체 좋아요 목록 조회 시 마지막 페이지까지 순회 조회 되는지 검증")
      @Test
      public void getPostLikers_shouldIteratorAllPages_whenUsingCursorPagination()
          throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        LocalDateTime cursorTimestamp = null;
        String cursorId = null;
        int limit = 10;
        int size = 0;

        //when
        //then
        while (true) {
          CommonResponse<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
              accessToken,
              ACTIVE_POST_ID, ACTIVE_COMMENT_WITH_LIKES_ID, cursorTimestamp, cursorId, limit,
              status().isOk()
          );
          size += getPostCommentLikersResponse.getData().content().size();

          if (getPostCommentLikersResponse.getData().nextCursor().cursorId() == null) {
            break;
          }
          cursorId = getPostCommentLikersResponse.getData().nextCursor().cursorId();
          cursorTimestamp = getPostCommentLikersResponse.getData().nextCursor().cursorTimestamp();
        }

        assertThat(size).isEqualTo(COMMENT_LIKE_USER_COUNT);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] getPostLikers - 존재하지 않는 댓글에 대한 요청 시 404 Not Found, 실패해야한다*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 댓글에 좋아요 요청 시 404 Not Found")
      @Test
      public void getPostLikers_shouldReturnNotFound_whenPostCommentLNotExists() throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String commentId = "comment-without-likers";

        //when
        //then
        CommonResponse<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
            accessToken,
            ACTIVE_POST_ID, commentId, null, null, 10, status().isNotFound()
        );
        assertThat(getPostCommentLikersResponse.isSuccess()).isFalse();
        assertThat(getPostCommentLikersResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_NOT_FOUND.getCode());
      }

      /*[Case #2] getPostLikers - 비활성 상태의 댓글에 대한 요청 시 400 BadRequest, 실패해야한다*/
      @Order(2)
      @DisplayName("2. DELETED 상태의 댓글에 대한 요청 시 400 BadRequest")
      @Test
      public void getPostLikers_shouldReturnBadRequest_whenPostCommentIsDeleted()
          throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
            accessToken,
            ACTIVE_POST_ID, DELETED_COMMENT_ID, null, null, 10, status().isBadRequest()
        );

        assertThat(getPostCommentLikersResponse.isSuccess()).isFalse();
        assertThat(getPostCommentLikersResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_NOT_ACCESSIBLE.getCode());
      }
    }
  }
}
