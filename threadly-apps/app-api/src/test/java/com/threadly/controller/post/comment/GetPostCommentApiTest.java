package com.threadly.controller.post.comment;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 조회 관련 API 테스트
 */
public class GetPostCommentApiTest extends BasePostApiTest {

  static final String POST_WITH_COMMENT_ID = "post_with_comments";


  /**
   * 게시글 댓글 목록 커서 기반 조회
   *
   * @throws Exception
   */
  /*[Case #1] getPostCommentList - 첫 페이지 응답에서 커서가 정상적으로 설정되어야 한다*/
  @DisplayName("게시글 댓글 목록 조회 - 첫 페이지 응답에서 커서와 응답이 정상적으로 설정되어야 한다")
  @Test
  public void getPostCommentList_shouldReturnNextCursor_whenFirstPageRequested() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);


    //when
    //then
    LocalDateTime cursorCommentedAt = null;
    String cursorCommentId = null;
    int limit = 10;

    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
        accessToken, POST_WITH_COMMENT_ID, cursorCommentedAt, cursorCommentId, limit, status().isOk()
    );
    assertThat(getPostCommentListResponse.getData().nextCursor().cursorCommentedAt()).isEqualTo(
        getPostCommentListResponse.getData().nextCursor().cursorCommentedAt());
    assertThat(getPostCommentListResponse.getData().nextCursor().cursorCommentId()).isEqualTo(
        getPostCommentListResponse.getData().nextCursor().cursorCommentId());
  }

  /*[Case #2] getPostCommentList - 게시글 댓글 목록 전체 조회 검증*/
  @DisplayName("게시글 댓글 목록 조회 - 커서 기반 페이징으로 전체 댓글이 조회되어야 한다")
  @Test
  public void getPostCommentList_shouldReturnAllComments() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = POST_WITH_COMMENT_ID;

    //when
    //then
    LocalDateTime cursorCommentedAt = null;
    String cursorCommentId = null;
    int limit = 10;
    int size = 0;

    while (true) {
      CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
          accessToken, postId, cursorCommentedAt, cursorCommentId, limit, status().isOk()
      );

      /*사이즈 증가*/
      size += getPostCommentListResponse.getData().comments().size();


      /*마지막 페이지일경우 종료*/
      if (getPostCommentListResponse.getData().nextCursor().cursorCommentId() == null) {
        break;
      }

      /*커서 검증*/
      assertThat(getPostCommentListResponse.getData().comments().getLast().commentId()).isEqualTo(
          getPostCommentListResponse.getData().nextCursor().cursorCommentId());
      assertThat(getPostCommentListResponse.getData().comments().getLast().commentedAt()).isEqualTo(
          getPostCommentListResponse.getData().nextCursor().cursorCommentedAt());

      /*커서 지정*/
      cursorCommentId = getPostCommentListResponse.getData().nextCursor().cursorCommentId();
      cursorCommentedAt = getPostCommentListResponse.getData().nextCursor().cursorCommentedAt();
    }
    assertThat(size).isEqualTo(POST_WITH_COMMENTS);
  }

  /*[Case #3] getPostCommentList - 댓글 좋아요 요청 후 댓글 목록 조회 시 liked 필드가 true로 반영되어야 한다*/
  @DisplayName("게시글 댓글 목록 조회 - 댓글 좋아요 요청 후 댓글 목록 조회 시 liked 필드가 true를 반환해야 한다")
  @Test
  public void getPostCommentList_shouldReflectLikedField_whenLikeRequest() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = POST_WITH_COMMENT_ID;

    //when
    //then
    LocalDateTime cursorCommentedAt = null;
    String cursorCommentId = null;
    int limit = 10;

    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse1 = sendGetPostCommentListRequest(
        accessToken, postId, cursorCommentedAt, cursorCommentId, limit, status().isOk()
    );

    String commentId = getPostCommentListResponse1.getData().comments().get(3).commentId();

    /*좋아요 요청*/
    sendLikePostCommentRequest(
        accessToken, postId, commentId, status().isOk()
    );

    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse2 = sendGetPostCommentListRequest(
        accessToken, postId, cursorCommentedAt, cursorCommentId, limit, status().isOk()
    );

    assertThat(getPostCommentListResponse2.getData().comments().getFirst().liked()).isNotEqualTo(
        getPostCommentListResponse1.getData().comments().getFirst().liked());
    assertThat(getPostCommentListResponse2.getData().comments().getFirst().liked()).isTrue();
  }

  /*[Case #4] getPostCommentList - ARCHIVED 상태가 아닌 게시글에 대한 댓글 목록 요청 시 400 Bad Request, 실패해야 한다*/
  @DisplayName("게시글 댓글 목록 조회 - 비활성화된 게시글의 댓글 목록을 조회 요청할 경우 Bad Request, 실패해야한다")
  @Test
  public void getPostCommentList_shouldReturnBadRequest_whenPostNotActive() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = DELETED_POSTS.getFirst().get("postId");

    //when
    //then
    LocalDateTime cursorCommentedAt = null;
    String cursorCommentId = null;
    int limit = 10;

    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
        accessToken, postId, cursorCommentedAt, cursorCommentId, limit, status().isBadRequest()
    );

    assertThat(getPostCommentListResponse.isSuccess()).isFalse();
    assertThat(getPostCommentListResponse.getCode()).isEqualTo(
        ErrorCode.POST_NOT_ACCESSIBLE.getCode());
  }

  /*[Case #5] getPostCommentList - 존재하지 않는 게시글에 대한 댓글 목록을 요청 시 404 Not Found, 실패해야한다*/
  @DisplayName("게시글 댓글 목록 조회 - 존재하지 않는 게시글에 대한 댓글 목록을 요청 시 Not Found, 실패해야 한다")
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

