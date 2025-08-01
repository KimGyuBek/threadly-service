package com.threadly.post.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.post.create.CreatePostApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

/**
 * 게시글 생성 관련 API 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class CreatePostApiTest extends BasePostApiTest {

  @Order(1)
  @DisplayName("게시글 생성 테스트")
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class createPostTest {

    @Order(1)
    @Nested
    @DisplayName("성공")
    class success {

      /*[Case #1] 게시글 작성 성공 시 요청한 content가 응답에 포함된다*/
      @DisplayName("1. 정상적으로 작성되면 요청한 content가 응답에 포함된다")
      @Test
      public void createPost_shouldCreatedContent_whenCreatePostWithValidInput() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        /*게시글 생성*/
        String content = "content";

        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
            accessToken, "content", List.of(), status().isCreated());

        //then
        assertThat(response.getData().content()).isEqualTo(content);
      }
    }

    @Order(2)
    @Nested
    @DisplayName("실패")
    class fail {

      /*[Case #1] 게시글 작성 요청 시 content가 비어있을 경우 실패한다*/
      @DisplayName("1.게시글 작성 요청 시 content가 비어있을 경우")
      @Test
      public void createPost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        /*게시글 생성*/
        String content = "";

        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
            accessToken, content, List.of(), status().isBadRequest());

        //then
        assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
      }

      /*[Case #2] 게시글 내용이 최대 길이를 초과할 경우 400 BadRequest*/
      @DisplayName("2. 게시글 내용이 최대 길이를 초과할 경우 400 BadRequest")
      @Test
      public void createPost_shouldReturnBadRequest_whenContentExceedsMaxLength() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        /*게시글 생성*/
        String content = "a".repeat(1001);

        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
            accessToken, content, List.of(), status().isBadRequest());

        //then
        assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
      }
    }
  }
}