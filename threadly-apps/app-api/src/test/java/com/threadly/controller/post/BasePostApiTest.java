package com.threadly.controller.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.controller.post.request.CreatePostCommentRequest;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.controller.post.request.UpdatePostRequest;
import com.threadly.post.comment.response.CreatePostCommentApiResponse;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Base Post Api Test
 */
public abstract class BasePostApiTest extends BaseApiTest {

  /**
   * 로그인 후 accessToken 추출
   *
   * @param email
   * @return
   * @throws Exception
   */
  public String getAccessToken(String email) throws Exception {
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
  public CommonResponse<CreatePostApiResponse> sendCreatePostRequest(String accessToken,
      String content, ResultMatcher expectedStatus) throws Exception {

    String requestBody = generateRequestBody(new CreatePostRequest(content));
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    return
        sendPostRequest(requestBody, "/api/posts",
            expectedStatus, new TypeReference<>() {
            }, headers);

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
  public CommonResponse<UpdatePostApiResponse> sendUpdatePostRequest(String accessToken,
      String modifiedContent, String postId, ResultMatcher expectedStatus) throws Exception {
    String requestBody = generateRequestBody(new UpdatePostRequest(modifiedContent));
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    return
        sendPatchRequest(requestBody,
            "/api/posts/" + postId,
            expectedStatus, new TypeReference<>() {
            }, headers);

  }

  /**
   * 게시글 조회 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<PostDetailApiResponse> sendGetPostRequest(String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    CommonResponse<PostDetailApiResponse> postDetailResponse = sendGetRequest(
        accessToken, "/api/posts/" + postId, expectedStatus,
        new TypeReference<>() {
        });

    return postDetailResponse;
  }

  /**
   * 게시글 목록 조회 요청 전송
   *
   * @param accessToken
   * @param expectedStatus
   * @return
   */
  public CommonResponse<PostDetailListApiResponse> sendGetPostListRequest(String accessToken,
      ResultMatcher expectedStatus) throws Exception {
    return
        sendGetRequest(
            accessToken, "/api/posts", expectedStatus,
            new TypeReference<>() {
            });
  }

  /**
   * 게시글 삭제 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendDeletePostRequest(String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    return
        sendDeleteRequest("",
            "/api/posts/" + postId,
            expectedStatus, new TypeReference<>() {
            }, headers);
  }

  /**
   * 게시글 댓글 작성 요청 전송
   */
  public CommonResponse<CreatePostCommentApiResponse> sendCreatePostCommentRequest(
      String accessToken,
      String postId, String content, ResultMatcher expectedStatus) throws Exception {
    String requestBody = generateRequestBody(new CreatePostCommentRequest(content));
    return
        sendPostRequest(
            requestBody,
            "/api/posts/" + postId + "/comments",
            expectedStatus,
            new TypeReference<CommonResponse<CreatePostCommentApiResponse>>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 게시글 삭제 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendDeletePostCommentRequest(String accessToken,
      String postId, String commentId, ResultMatcher expectedStatus) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    return
        sendDeleteRequest("",
            "/api/posts/" + postId + "/comments/" + commentId,
            expectedStatus, new TypeReference<>() {
            }, headers);
  }
  /*게시글*/
  //[ACTIVE] 상태의 게시글
  public static final List<Map<String, String>> ACTIVE_POSTS = List.of(
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post1"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post2"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post3"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post4"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post5"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post6"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post7"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post8"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post9"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post10"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post11"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post12"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post13"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post14"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post15"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post16"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post17"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post18"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post19"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post20"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post41"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post42"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post43"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post44"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post45"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post46"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post47"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post48"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post49"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post50")
  );

  // DELETED 상태의 게시글
  public static final List<Map<String, String>> DELETED_POSTS = List.of(
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post21"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post22"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post23"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post24"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post25"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post26"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post27"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post28"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post29"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post30")
  );

  // BLOCKED 상태의 게시글
  public static final List<Map<String, String>> BLOCKED_POSTS = List.of(
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post31"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post32"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post33"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post34"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post35")
  );

  // ARCHIVED 상태의 게시글
  public static final List<Map<String, String>> ARCHIVED_POSTS = List.of(
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post36"),
      Map.of("userId", "usr4", "userEmail", "sunny@test.com", "postId", "post37"),
      Map.of("userId", "usr5", "userEmail", "noodle@test.com", "postId", "post38"),
      Map.of("userId", "usr1", "userEmail", "user_email_verified1@test.com", "postId", "post39"),
      Map.of("userId", "usr2", "userEmail", "user_email_verified2@test.com", "postId", "post40")
  );


  /*댓글*/
  // ACTIVE 상태의 댓글
  public static final List<Map<String, String>> ACTIVE_COMMENTS = List.of(
      Map.of("commentId", "cmt1", "postId", "post1", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt3", "postId", "post2", "userId", "usr5", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt5", "postId", "post3", "userId", "usr5", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt6", "postId", "post3", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt7", "postId", "post3", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt10", "postId", "post3", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt11", "postId", "post4", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt12", "postId", "post6", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt13", "postId", "post6", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt15", "postId", "post6", "userId", "usr4", "userEmail", "sunny@test.com")
  );

  // BLOCKED 상태의 댓글
  public static final List<Map<String, String>> BLOCKED_COMMENTS = List.of(
      Map.of("commentId", "cmt2", "postId", "post1", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt4", "postId", "post3", "userId", "usr5", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt9", "postId", "post3", "userId", "usr5", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt16", "postId", "post6", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt24", "postId", "post8", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt27", "postId", "post8", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt35", "postId", "post9", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt39", "postId", "post10", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt45", "postId", "post11", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt54", "postId", "post12", "userId", "usr4", "userEmail", "sunny@test.com")
  );

  // DELETED 상태의 댓글
  public static final List<Map<String, String>> DELETED_COMMENTS = List.of(
      Map.of("commentId", "cmt8", "postId", "post3", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt14", "postId", "post6", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt20", "postId", "post7", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt22", "postId", "post8", "userId", "usr4", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt36", "postId", "post9", "userId", "usr5", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt37", "postId", "post9", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt47", "postId", "post11", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt55", "postId", "post12", "userId", "usr2", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt74", "postId", "post16", "userId", "usr1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt79", "postId", "post16", "userId", "usr2", "userEmail", "user_email_verified2@test.com")
  );

}