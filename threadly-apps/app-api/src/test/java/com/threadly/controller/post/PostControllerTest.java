package com.threadly.controller.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.controller.post.request.UpdatePostRequest;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Post Controller Test
 */
class PostControllerTest extends BaseApiTest {


  /**
   * createPost() - 게시글 생성 테스트
   */
  /*[Case #1] 게시글 작성 성공 시 요청한 content가 응답에 포함된다*/
  @DisplayName("createPost - 정상적으로 작성되면 요청한 content가 응답에 포함된다")
  @Test
  public void createPost_shouldCreatedContent_whenCreatePostWithValidInput() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

    //when
    /*게시글 생성*/
    String content = "content";

    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
        accessToken, "content", status().isCreated());

    //then
    assertThat(response.getData().content()).isEqualTo(content);
  }

  /*[Case #2] 게시글 작성 요청 시 content가 비어있을 경우 실패한다*/
  @DisplayName("createPost - 게시글 작성 요청 시 content가 비어있으면 실패한다")
  @Test
  public void createPost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

    //when
    /*게시글 생성*/
    String content = "";

    CommonResponse<CreatePostApiResponse> response = sendCreatePostRequest(
        accessToken, content, status().isBadRequest());

    //then
    assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
  }

  /**
   * updatePost() - 게시글 업데이트 테스트
   */
  /*[Case #1] 작성자가 수정 요청 시 정상적으로 수정되어야 한다 */
  @DisplayName("updatePost - 작성자가 수정 요청 시 정상적으로 수정된다")
  @Test
  public void updatePost_shouldUpdatePostSuccessfully_whenWriterRequestsUpdate() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

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
  @DisplayName("updatePost - 작성자가 아닌 사용자가 수정 요청 시 403 Forbidden 응답이 반환된다")
  @Test
  public void updatePost_shouldReturnForbidden_whenNonWriterTriesToUpdatePost() throws Exception {
    //given
    /*로그인*/
    String accessToken1 = getAccessToken(USER_EMAIL_VERIFIED_1);
    String accessToken2 = getAccessToken(USER_EMAIL_VERIFIED_2);

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
  @DisplayName("updatePost - 작성자가 존재하지 않는 postId로 수정 요청 시 ")
  @Test
  public void updatePost_shouldReturnNotFound_whenRequestNotExistsPostId() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

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
  @DisplayName("updatePost - 작성자가 비어있는 content로 요청 시 실패해야한다")
  @Test
  public void updatePost_shouldReturnBadRequest_whenContentIsBlank() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

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

  /**
   * getPost() - 게시글 조회 테스트
   */
  /*[Case #1] getPost()  존재하는 게시글을 조회하면 200 ok 응답을 반환한다.*/
  @DisplayName("getPost - 존재하는 게시글을 조회하면 200 OK 응답을 반환한다")
  @Test
  public void getPost_shouldReturnOk_whenGetExistingPost() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

    //when
    //then
    /*게시글 조회*/
    String postId = "post1";
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetRequest(
        accessToken, "/api/posts/" + postId, status().isOk(),
        new TypeReference<>() {
        });

    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
  }

  /*[Case #2] 존재하지 않는 게시글을 조회하면 404 NOT FOUND 응답을 반환한다.*/
  @DisplayName("getPost() - 존재하지 않는 게시글을 조회하면 404 Not Found가 반환되어야 한다")
  @Test
  public void getPost_shouldReturnNotFound_whenGetNotExistingPost() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

    //when
    //then
    /*게시글 조회*/
    String postId = "post123";
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetRequest(
        accessToken, "/api/posts/" + postId, status().isNotFound(),
        new TypeReference<>() {
        });

    assertThat(postDetailResponse.isSuccess()).isFalse();
    assertThat(postDetailResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
  }
  /*[Case #3]  게시글 작성 후 조회시 작성한 내용과 동일해야함*/
  @DisplayName("getPost() - 게시글 작성 후 조회 시 작성한 내용과 postId가 동일해야한다")
  @Test
  public void getPost_shouldReturnSameContentAndId_whenGetPostAfterCreation() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

    /*게시글 작성*/
    String content = "content";
    CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(accessToken,
        content,
        status().isCreated());

    //when
    /*게시글 조회*/
    String postId = createPostResponse.getData().postId();
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetRequest(
        accessToken, "/api/posts/" + postId, status().isOk(),
        new TypeReference<>() {
        });

    //then
    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
    assertThat(postDetailResponse.getData().content()).isEqualTo(content);
  }

  /*[Case #4]  게시글 작성 후 조회, 내용 수정 시 작성한 내용과 동일해야함*/
  @DisplayName("getPost() - 게시글 작성 후 수정 시, 조회하면 수정된 내용이 조회되어야 한다")
  @Test
  public void getPost_shouldReturnSameContentAndId_whenGetPostAfterUpdate() throws Exception {
    //given
    /*로그인*/
    String accessToken = getAccessToken(USER_EMAIL_VERIFIED_1);

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
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetRequest(
        accessToken, "/api/posts/" + postId, status().isOk(),
        new TypeReference<>() {
        });

    //then
    assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
    assertThat(postDetailResponse.getData().content()).isEqualTo(modifiedContent);
  }


  /**
   * 로그인 후 accessToken 추출
   *
   * @param email
   * @return
   * @throws Exception
   */
  private String getAccessToken(String email) throws Exception {
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        email, PASSWORD, new TypeReference<>() {
        },
        status().isOk()
    );
    return loginResponse.getData().accessToken();
  }

  /**
   * 게시글 등록 요청 전송
   *
   * @param content
   * @param expectedStatus
   * @return
   */
  private CommonResponse<CreatePostApiResponse> sendCreatePostRequest(String accessToken,
      String content, ResultMatcher expectedStatus) throws Exception {

    String requestBody = generateRequestBody(new CreatePostRequest(content));
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    CommonResponse<CreatePostApiResponse> response = sendPostRequest(requestBody, "/api/posts",
        expectedStatus, new TypeReference<>() {
        }, headers);

    return response;
  }

  /**
   * 게시글 수정 요청 전송
   *
   * @param accessToken
   * @param modifiedContent
   * @param postId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  private CommonResponse<UpdatePostApiResponse> sendUpdatePostRequest(String accessToken,
      String modifiedContent, String postId, ResultMatcher expectedStatus) throws Exception {
    String requestBody = generateRequestBody(new UpdatePostRequest(modifiedContent));
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendPatchRequest(requestBody,
        "/api/posts/" + postId,
        expectedStatus, new TypeReference<>() {
        }, headers);

    return updatedPostResponse;
  }
}