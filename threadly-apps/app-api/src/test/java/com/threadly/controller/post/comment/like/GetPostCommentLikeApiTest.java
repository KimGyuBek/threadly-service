package com.threadly.controller.post.comment.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.controller.post.BasePostApiTest;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import com.threadly.post.like.comment.GetPostCommentLikersApiResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 댓글 좋아요 조회 관련 API 테스트
 */
public class GetPostCommentLikeApiTest extends BasePostApiTest {

  /**
   * getPostLikers() 테스트
   *
   * @throws Exception
   */
  /*[Case #1] getPostLikers - 좋아요가 있는 댓글의 목록을 조회할 경우 첫 페이지 응답에서 커서가 정상적으로 설정되어야한다*/
  @DisplayName("게시글 댓글 좋아요 목록 조회 - 좋아요가 존재하는 게시글에 요청을 보냈을때 커서가 정상적으로 설정되어야한다")
  @Test
  public void getPostCommentLikers_shouldReturnFirstPage() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = "post_with_comments";

    /*게시글 댓글 목록 조회*/
    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
        accessToken,
        postId,
        null,
        null,
        10,
        status().isOk()
    );

    //when
    //then
    /*게시글 댓글 좋아요 목록 조회 요청*/
    String commentId = getPostCommentListResponse.getData().comments().getFirst().commentId();
    CommonResponse<GetPostCommentLikersApiResponse> getPostCommentLikerListResponse = sendGetPostCommentLikersRequest(
        accessToken,
        postId,
        commentId,
        null,
        null, 10, status().isOk()
    );

//    커서 검증
    assertThat(getPostCommentLikerListResponse.getData().likers().getLast().likedAt()).isEqualTo(
        getPostCommentLikerListResponse.getData().nextCursor().cursorLikedAt());
    assertThat(getPostCommentLikerListResponse.getData().likers().getLast().likerId()).isEqualTo(
        getPostCommentLikerListResponse.getData().nextCursor().cursorLikerId());
  }

  /*[Case #2] getPostLikers - 존재하지 않는 댓글에 대한 요청 시 404 Not Found, 실패해야한다*/
  @DisplayName("게시글 댓글 좋아요 목록 조회 - 존재하지 않는 댓글에 대한 요청 시 404 Not Found 실패해야한다")
  @Test
  public void getPostLikers_shouldReturnNotFound_whenPostCommentLNotExists() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String commentId = "comment-without-likers";
    String postId = "post_with_comments";

    //when
    //then
    CommonResponse<GetPostCommentLikersApiResponse> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
        accessToken,
        postId, commentId, null, null, 10, status().isNotFound()
    );
    assertThat(getPostCommentLikersResponse.isSuccess()).isFalse();
    assertThat(getPostCommentLikersResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_NOT_FOUND.getCode());
  }

  /*[Case #3] getPostLikers - 비활성 상태의 댓글에 대한 요청 시 400 BadRequest, 실패해야한다*/
  @DisplayName("게시글 댓글 좋아요 목록 조회 - 비활성 상태의 댓글에 대한 요청 시 400 BadRequest, 실패해야한다")
  @Test
  public void getPostLikers_shouldReturnBadRequest_whenPostCommentIsNotActive() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = DELETED_COMMENTS.getFirst().get("postId");
    String commentId = DELETED_COMMENTS.getFirst().get("commentId");

    //when
    //then
    CommonResponse<GetPostCommentLikersApiResponse> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
        accessToken,
        postId, commentId, null, null, 10, status().isBadRequest()
    );

    assertThat(getPostCommentLikersResponse.isSuccess()).isFalse();
    assertThat(getPostCommentLikersResponse.getCode()).isEqualTo(
        ErrorCode.POST_COMMENT_NOT_ACCESSIBLE.getCode());
  }

  /*[Case #4] getPostLikers - 좋아요가 없는 댓글에 대한 요청 시 빈 리스트가 반환되어야한다.*/
  @DisplayName("게시글 댓글 좋아요 목록 조회 - 좋아요가 없는 댓글에 대한 요청 시 빈 리스트가 반환되어야한다.")
  @Test
  public void getPostLikers_shouldReturnEmptyList_whenPostCommentHasNotLikes() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = "post_with_comments";
    CommonResponse<GetPostCommentListApiResponse> getPostCommentListRequest = sendGetPostCommentListRequest(
        accessToken, postId, null, null, 10, status().isOk());

    String commentId = "post_comment_without_likers";

    //when
    //then
    CommonResponse<GetPostCommentLikersApiResponse> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
        accessToken,
        postId, commentId, null, null, 10, status().isOk()
    );

    assertThat(getPostCommentLikersResponse.getData().likers()).isEmpty();
  }

  /*[Case #5] getPostLikers - 전체 좋아요 목록 조회 시 마지막 페이지까지 순회 조화한다*/
  @DisplayName("게시글 댓글 좋아요 목록 조회 - 전체 좋아요 목록 조회 시마지막페이지까지 순회 조회한다")
  @Test
  public void getPostLikers_shouldIteratorAllPages_whenUsingCursorPagination() throws Exception {
    //given

    /*로그인 요청*/
    String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

    String postId = "post_with_comments";
    CommonResponse<GetPostCommentListApiResponse> getPostCommentListResponse = sendGetPostCommentListRequest(
        accessToken, postId, null, null, 10, status().isOk());

    String commentId = getPostCommentListResponse.getData().comments().getFirst().commentId();

    LocalDateTime cursorLikedAt = null;
    String cursorLikerId = null;
    int limit = 10;
    int size = 0;

    //when
    //then
    while (true) {
      CommonResponse<GetPostCommentLikersApiResponse> getPostCommentLikersResponse = sendGetPostCommentLikersRequest(
          accessToken,
          postId, commentId, cursorLikedAt, cursorLikerId, limit, status().isOk()
      );
      size += getPostCommentLikersResponse.getData().likers().size();

      if (getPostCommentLikersResponse.getData().nextCursor().cursorLikerId() == null) {
        break;
      }
      cursorLikerId = getPostCommentLikersResponse.getData().nextCursor().cursorLikerId();
      cursorLikedAt = getPostCommentLikersResponse.getData().nextCursor().cursorLikedAt();
    }

    assertThat(size).isEqualTo(34);
  }
}
