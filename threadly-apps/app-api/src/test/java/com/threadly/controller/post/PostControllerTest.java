package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.post.response.CreatePostApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Post Controller Test
 */
class PostControllerTest extends BaseApiTest {


  /**
   * createPost()
   */
  @Test
  public void createNewContent_shouldSuccess() throws Exception {
    //given
    /*로그인*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        USER_EMAIL_VERIFIED, PASSWORD, new TypeReference<>() {
        },
        status().isOk()
    );


    //when
    /*게시글 생성*/
    String content = "content";
    String requestBody = generateRequestBody(new CreatePostRequest(content));
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + loginResponse.getData().accessToken());

    CommonResponse<CreatePostApiResponse> response = sendPostRequest(requestBody, "/api/posts",
        status().isCreated(), new TypeReference<>() {
        }, headers);

    //then
    assertThat(response.getData().content()).isEqualTo(content);
  }
}