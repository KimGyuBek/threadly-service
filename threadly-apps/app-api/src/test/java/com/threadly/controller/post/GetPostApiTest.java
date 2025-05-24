package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.PostEngagementApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 조회 관련 API 테스트
 */
class GetPostApiTest extends BasePostApiTest {


  /**
   * getPost() - 게시글 조회 테스트
   */
  /*[Case #1] getPost()  존재하는 게시글을 조회하면 200 ok 응답을 반환한다.*/
  @DisplayName("게시글 조회 - 존재하는 게시글을 조회하면 200 OK 응답을 반환한다")
  @Test
  public void getPost_shouldReturnOk_whenGetExistingPost() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    /*게시글 조회*/
    String postId = "post1";
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetPostRequest(
        accessToken, postId, status().isOk());

    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
  }

  /*[Case #2] 존재하지 않는 게시글을 조회하면 404 NOT FOUND 응답을 반환한다.*/
  @DisplayName("게시글 조회 - 존재하지 않는 게시글을 조회하면 404 Not Found가 반환되어야 한다")
  @Test
  public void getPost_shouldReturnNotFound_whenGetNotExistingPost() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    /*게시글 조회*/
    String postId = "post12300";
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetPostRequest(
        accessToken, postId, status().isNotFound());

    assertThat(postDetailResponse.isSuccess()).isFalse();
    assertThat(postDetailResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
  }

  /*[Case #3]  게시글 작성 후 조회시 작성한 내용과 동일해야함*/
  @DisplayName("게시글 조회 - 게시글 작성 후 조회 시 작성한 내용과 postId가 동일해야한다")
  @Test
  public void getPost_shouldReturnSameContentAndId_whenGetPostAfterCreation() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    /*게시글 작성*/
    String content = "content";
    CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(accessToken,
        content,
        status().isCreated());

    //when
    /*게시글 조회*/
    String postId = createPostResponse.getData().postId();
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetPostRequest(
        accessToken, postId, status().isOk());

    //then
    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
    assertThat(postDetailResponse.getData().content()).isEqualTo(content);
  }

  /*[Case #4]  게시글 작성 후 조회, 내용 수정 시 작성한 내용과 동일해야함*/
  @DisplayName("게시글 조회 - 게시글 작성 후 수정 시, 조회하면 수정된 내용이 조회되어야 한다")
  @Test
  public void getPost_shouldReturnSameContentAndId_whenGetPostAfterUpdate() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    /*게시글 작성*/
    String content = "content";
    CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(accessToken,
        content,
        status().isCreated());

    /*게시글 수정*/
    String postId = createPostResponse.getData().postId();
    /*게시글 수정 요청 전송*/
    String modifiedContent = "modifiedContent";
    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
        accessToken, modifiedContent, postId, status().isOk());

    //when
    /*게시글 조회*/
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetPostRequest(
        accessToken, postId, status().isOk());

    //then
    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
    assertThat(postDetailResponse.getData().content()).isEqualTo(modifiedContent);
  }


  /**
   * getPostList() - 테스트
   */
  /*[Case #1] getPostList()  게시글 리스트 조회 시 200 OK 반환*/
  @DisplayName("게시글 목록 조회 -  게시글 리스트  조회 시 200 OK 응답을 반환한다")
  @Test
  public void getPostList_shouldReturnOk_whenGetExistingPost() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    /*게시글 목록 조회*/
    LocalDateTime cursorPostedAt = null;
    String cursorPostId = null;

    CommonResponse<PostDetailListApiResponse> postListResponse1 = sendGetPostListRequest(
        accessToken, cursorPostedAt, cursorPostId, 10, status().isOk());

    cursorPostedAt = postListResponse1.getData().nextCursor().postedAt();
    cursorPostId = postListResponse1.getData().nextCursor().postId();

    CommonResponse<PostDetailListApiResponse> postListResponse2 = sendGetPostListRequest(
        accessToken, cursorPostedAt, cursorPostId, 10, status().isOk());

    assertThat(postListResponse1.getData().posts()).hasSize(10);

    assertThat(postListResponse2.getData().posts()).hasSize(10);
    assertThat(postListResponse1.getData().posts().getLast().postId()).isEqualTo(
        postListResponse1.getData().nextCursor().postId());
  }

  /*[Case #2] getPostList - 게시글 목록 전체 조회 - 커서 기반 페이징 방식으로 전체 페이지를 순회하면서 마지막 페이지까지 반복 조회하는 테스트
   * nextCursor가 null이 되는 시점까지 반복적으로 조회 요청을 전송함으로써 페이징 커서의 정확성과 순회 종료 조건을 검증한다
   * */

  @DisplayName("게시글 목록 조회 -  커서 기반 페이징으로 전체 게시글 마지막 페이지까지 순회하며 조회한다")
  @Test
  public void getPostList_shouldIterateAllPages_usingCursorPagination() throws Exception {
    //given
    /*로그인 요청*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    //then
    /*게시글 전체 조회 요청*/
    LocalDateTime cursorPostedAt = null;
    String cursorPostId = null;
    int limit = 10;
    int size = 0;

    while (true) {
      CommonResponse<PostDetailListApiResponse> getPostListResponse = sendGetPostListRequest(
          accessToken, cursorPostedAt, cursorPostId, limit, status().isOk());
      size += getPostListResponse.getData().posts().size();

      /*마지막 페이지면 */
      if (getPostListResponse.getData().nextCursor().postedAt() == null) {
        break;
      }

      cursorPostedAt = getPostListResponse.getData().nextCursor().postedAt();
      cursorPostId = getPostListResponse.getData().nextCursor().postId();
    }

    assertThat(size).isEqualTo(ACTIVE_POST_COUNT);
  }

  /**
   * getPostEngagement() - 테스트
   */
  /*[Case #1] getPostEngagement - 게시글 좋아요 요약 조회시 성공해야 한다*/
  @DisplayName("게시글 좋아요 요약 조회 - 좋아요가 존재하는 게시글에 요청을 보내면 likeCount가 정확하게 반환되어야 한다")
  @Test
  public void getPostEngagement_shouldReturnCorrectLikeCount_whenPostHasLikes() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());
    String postId = "post10";

    long likeCount = POST_LIKES.get(postId);

    //when
    //then
    /*조회 요청*/
    CommonResponse<PostEngagementApiResponse> postEngagementResponse = sendGetPostEngagementRequest(
        accessToken, "post10", status().isOk()
    );
    assertThat(postEngagementResponse.getData().likeCount()).isEqualTo(likeCount);
  }

  /*[Case #2] getPostEngagement - 존재하지 않는 게시글 조회 시 404 Not Found, 실패해야 한다*/
  @DisplayName("게시글 좋아요 요약 조회 - 존재하지 않는 게시글 조회 시 404 Not Found를 응답한다 ")
  @Test
  public void getPostEngagement_shouldNotFound_whenPostNotExists() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());
    String postId = "post_not_exists";

    long likeCount = POST_LIKES.get("post10");

    //when
    //then
    /*조회 요청*/
    CommonResponse<PostEngagementApiResponse> postEngagementResponse = sendGetPostEngagementRequest(
        accessToken, postId, status().isNotFound()
    );
    assertThat(postEngagementResponse.isSuccess()).isFalse();
    assertThat(postEngagementResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
  }

  /*[Case #3] getPostEngagement - 좋아요가 없는 게시글 조회 시 likeCount가 0이어야 한다*/
  @DisplayName("게시글 좋아요 요약 조회 - 좋아요가 없는 게시글이면 likeCount는 0이다")
  @Test
  public void getPostEngagement_shouldReturnZeroLikes_whenPostHasNoLikes() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());
    String postId = POST_ID_WITH_NO_LIKES.getFirst();

    //when
    //then
    /*조회 요청*/
    CommonResponse<PostEngagementApiResponse> postEngagementResponse = sendGetPostEngagementRequest(
        accessToken, postId, status().isOk()
    );
    assertThat(postEngagementResponse.getData().likeCount()).isEqualTo(0);
  }
}