package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 조회 관련 API 테스트
 */
class FetchPostApiTest extends BasePostApiTest {


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
    String postId = "post123";
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
   * getPostList() - 게시글 리스트 조회 테스트
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
    CommonResponse<PostDetailListApiResponse> postListResponse = sendGetPostListRequest(
        accessToken, status().isOk());

    assertThat(postListResponse.getData().posts()).hasSize(30);
  }

}