package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.update.UpdatePostApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 수정 관련 API 테스트
 */
class UpdatePostApiTest extends BasePostApiTest {

  /**
   * updatePost() - 게시글 업데이트 테스트
   */
  /*[Case #1] 작성자가 수정 요청 시 정상적으로 수정되어야 한다 */
  @DisplayName("updatePost - 작성자가 수정 요청 시 정상적으로 수정된다")
  @Test
  public void updatePost_shouldUpdatePostSuccessfully_whenWriterRequestsUpdate() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    String content = "content";
    String modifiedContent = "modifiedContent";

    /*게시글 생성*/
    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken, content,
        status().isCreated());
    String postId = response.getData().postId();

    //when
    /*게시글 수정 요청 전송*/
    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
        accessToken, modifiedContent, postId, status().isOk());

    //then
    assertThat(updatedPostResponse.getData().content()).isEqualTo(modifiedContent);
  }


  /*[Case #2]  작성자가 아닌 사용자가 게시글 수정 요청을 보내면 실패한다*/
  @DisplayName("게시글 수정 - 작성자가 아닌 사용자가 수정 요청 시 403 Forbidden 응답이 반환된다")
  @Test
  public void updatePost_shouldReturnForbidden_whenNonWriterTriesToUpdatePost() throws Exception {
    //given
    /*로그인*/
    String accessToken1 = getAccessToken(VERIFIED_USER_EMAILS.getFirst());
    String accessToken2 = getAccessToken(VERIFIED_USER_EMAILS.getLast());

    String content = "content";
    String modifiedContent = "modifiedContent";

    /*게시글 생성*/
    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken1, content,
        status().isCreated());
    String postId = response.getData().postId();

    //when
    /*게시글 수정 요청 전송*/
    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
        accessToken2, modifiedContent, postId, status().isForbidden());

    //then
    assertThat(updatedPostResponse.isSuccess()).isFalse();
    assertThat(updatedPostResponse.getCode()).isEqualTo(ErrorCode.POST_UPDATE_FORBIDDEN.getCode());
  }

  /*[Case #3] 작성자가 존재하지 않는 postId로 수정 요청 시 실패해야 한다*/
  @DisplayName("게시글 수정 - 작성자가 존재하지 않는 postId로 수정 요청 시 ")
  @Test
  public void updatePost_shouldReturnNotFound_whenRequestNotExistsPostId() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    String content = "content";
    String modifiedContent = "modifiedContent";

    /*게시글 생성*/
    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken, content,
        status().isCreated());

    //when
    /*게시글 수정 요청 전송*/
    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
        accessToken, modifiedContent, "pos123123", status().isNotFound());

    //then
    assertThat(updatedPostResponse.isSuccess()).isFalse();
    assertThat(updatedPostResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
  }

  /*[Case #4] 작성자가 빈 content로 수정 요청 시 실패해야 한다*/
  @DisplayName("게시글 수정 - 작성자가 비어있는 content로 요청 시 실패해야한다")
  @Test
  public void updatePost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    String content = "content";
    String modifiedContent = "";

    /*게시글 생성*/
    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken, content,
        status().isCreated());
    String postId = response.getData().postId();

    //when
    /*게시글 수정 요청 전송*/
    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
        accessToken, modifiedContent, "pos123123", status().isBadRequest());

    //then
    assertThat(updatedPostResponse.isSuccess()).isFalse();
    assertThat(updatedPostResponse.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
  }
//  private CommonResponse<>

}