package com.threadly.post.controller;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.core.port.post.in.create.CreatePostApiResponse;
import com.threadly.core.port.post.in.update.UpdatePostApiResponse;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 게시글 수정 관련 API 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UpdatePostApiTest extends BasePostApiTest {

  /**
   * updatePost() - 게시글 업데이트 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 업데이트 테스트")
  @Nested
  class updatePost {

    @Order(1)
    @DisplayName("성공")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class success {

      /*[Case #1] 작성자가 수정 요청 시 정상적으로 수정되어야 한다 */
      @DisplayName("1. 작성자가 수정 요청 시 정상적으로 수정되는지 검증")
      @Test
      public void updatePost_shouldUpdatePostSuccessfully_whenWriterRequestsUpdate()
          throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String content = "content";
        String modifiedContent = "modifiedContent";

        /*게시글 생성*/
        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
            accessToken,
            content,
            List.of(),
            status().isCreated());
        String postId = response.getData().postId();

        //when
        /*게시글 수정 요청 전송*/
        CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
            accessToken, modifiedContent, postId, status().isOk());

        //then
        assertThat(updatedPostResponse.getData().content()).isEqualTo(modifiedContent);
      }
    }

    @Order(2)
    @DisplayName("실패")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class fail {

      /*[Case #1]  작성자가 아닌 사용자가 게시글 수정 요청을 보내면 실패한다*/
      @Order(1)
      @DisplayName("1. 작성자가 아닌 사용자가 수정 요청 시 403 Forbidden")
      @Test
      public void updatePost_shouldReturnForbidden_whenNonWriterTriesToUpdatePost()
          throws Exception {
        //given
        /*로그인*/
        String accessToken1 = getAccessToken(EMAIL_VERIFIED_USER_1);
        String accessToken2 = getAccessToken(EMAIL_VERIFIED_USER_2);

        String content = "content";
        String modifiedContent = "modifiedContent";

        /*게시글 생성*/
        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken1,
            content,
            List.of(),
            status().isCreated());
        String postId = response.getData().postId();

        //when
        /*게시글 수정 요청 전송*/
        CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
            accessToken2, modifiedContent, postId, status().isForbidden());

        //then
        assertThat(updatedPostResponse.isSuccess()).isFalse();
        assertThat(updatedPostResponse.getCode()).isEqualTo(
            ErrorCode.POST_UPDATE_FORBIDDEN.getCode());
      }

      /*[Case #2] 작성자가 존재하지 않는 postId로 수정 요청 시 실패해야 한다*/
      @Order(2)
      @DisplayName("2. 작성자가 존재하지 않는 postId로 수정 요청 할 경우")
      @Test
      public void updatePost_shouldReturnNotFound_whenRequestNotExistsPostId() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String content = "content";
        String modifiedContent = "modifiedContent";

        /*게시글 생성*/
        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken,
            content,
            List.of(),
            status().isCreated());

        //when
        /*게시글 수정 요청 전송*/
        CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
            accessToken, modifiedContent, "pos123123", status().isNotFound());

        //then
        assertThat(updatedPostResponse.isSuccess()).isFalse();
        assertThat(updatedPostResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
      }

      /*[Case #3] 작성자가 빈 content로 수정 요청 시 실패해야 한다*/
      @Order(3)
      @DisplayName("3. 작성자가 비어있는 content로 요청 할 경우")
      @Test
      public void updatePost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String content = "content";
        String modifiedContent = "";

        /*게시글 생성*/
        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken,
            content,
            List.of(),
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

      /*[Case #4] 게시글 수정 내용이 최대 길이를 초과할 경우 400 BadRequest*/
      @Order(4)
      @DisplayName("4. 게시글 수정 내용이 최대 길이를 초과할 경우 400 BadRequest")
      @Test
      public void updatePost_shouldReturnBadRequest_whenContentExceedsMaxLength() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        String content = "content";
        String modifiedContent = "a".repeat(1001);

        /*게시글 생성*/
        CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(accessToken,
            content,
            List.of(),
            status().isCreated());
        String postId = response.getData().postId();

        //when
        /*게시글 수정 요청 전송*/
        CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
            accessToken, modifiedContent, postId, status().isBadRequest());

        //then
        assertThat(updatedPostResponse.isSuccess()).isFalse();
        assertThat(updatedPostResponse.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
      }

    }
  }
}