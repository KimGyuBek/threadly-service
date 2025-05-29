package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.create.CreatePostApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 게시글 생성 관련 API 테스트
 */
class CreatePostApiTest extends BasePostApiTest {

  /*[Case #1] 게시글 작성 성공 시 요청한 content가 응답에 포함된다*/
  @DisplayName("게시글 생성 - 정상적으로 작성되면 요청한 content가 응답에 포함된다")
  @Test
  public void createPost_shouldCreatedContent_whenCreatePostWithValidInput() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    /*게시글 생성*/
    String content = "content";

    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
        accessToken, "content", status().isCreated());

    //then
    assertThat(response.getData().content()).isEqualTo(content);
  }

  /*[Case #2] 게시글 작성 요청 시 content가 비어있을 경우 실패한다*/
  @DisplayName("게시글 생성 - 게시글 작성 요청 시 content가 비어있으면 실패한다")
  @Test
  public void createPost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(VERIFIED_USER_EMAILS.getFirst());

    //when
    /*게시글 생성*/
    String content = "";

    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
        accessToken, content, status().isBadRequest());

    //then
    assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
  }

}