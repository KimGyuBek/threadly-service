package com.threadly.controller.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.controller.post.request.CreatePostCommentRequest;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.controller.post.request.UpdatePostRequest;
import com.threadly.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.post.like.comment.GetPostCommentLikersApiResponse;
import com.threadly.post.like.comment.LikePostCommentApiResponse;
import com.threadly.post.like.post.GetPostLikersApiResponse;
import com.threadly.post.like.post.LikePostApiResponse;
import com.threadly.post.update.UpdatePostApiResponse;
import java.time.LocalDateTime;
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
  public CommonResponse<GetPostDetailApiResponse> sendGetPostRequest(String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    return
        sendGetRequest(
            accessToken, "/api/posts/" + postId, expectedStatus,
            new TypeReference<>() {
            });
  }

  /**
   * 게시글 좋아요 요약 조회 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetPostEngagementApiResponse> sendGetPostEngagementRequest(
      String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    return
        sendGetRequest(
            accessToken, "/api/posts/" + postId + "/engagement", expectedStatus,
            new TypeReference<>() {
            });

  }

  /**
   * 게시글 좋아요 목록 조회 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetPostLikersApiResponse> sendGetPostLikersRequest(String accessToken,
      String postId, LocalDateTime cursorLikedAt, String cursorLikerId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/engagement/likes";

    if (cursorLikedAt != null && cursorLikerId != null && cursorLikedAt != null) {
      path += "?cursor_liked_at=" + cursorLikedAt + "&cursor_liker_id=" + cursorLikerId + "&limit="
          + limit;

    }
    CommonResponse<GetPostLikersApiResponse> response = sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });

    return response;
  }

  /**
   * 게시글 목록 조회 요청 전송
   *
   * @param accessToken
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetPostDetailListApiResponse> sendGetPostListRequest(String accessToken,
      LocalDateTime cursorPostedAt, String cursorPostId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path =
        "/api/posts?limit=" + limit;

    if (cursorPostedAt != null || cursorPostId != null) {
      path += "&cursor_posted_at=" + cursorPostedAt + "&cursor_post_id=" + cursorPostId;
    }

    return
        sendGetRequest(
            accessToken, path, expectedStatus,
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
   * 게시글  좋아요 요청 전송
   */
  public CommonResponse<LikePostApiResponse> sendLikePostRequest(
      String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    return
        sendPostRequest(
            "",
            "/api/posts/" + postId + "/likes",
            expectedStatus,
            new TypeReference<CommonResponse<LikePostApiResponse>>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 게시글  좋아요 취소 요청 전송
   */
  public CommonResponse<LikePostApiResponse> sendCancelPostLikeRequest(
      String accessToken,
      String postId, ResultMatcher expectedStatus) throws Exception {
    return
        sendDeleteRequest(
            "",
            "/api/posts/" + postId + "/likes",
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 게시글 댓글 목록 커서 기반 조회 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetPostCommentListApiResponse> sendGetPostCommentListRequest(
      String accessToken,
      String postId, LocalDateTime cursorLikedAt, String cursorLikerId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/comments";

    if (cursorLikedAt != null && cursorLikerId != null && cursorLikedAt != null) {
      path += "?cursor_commented_at=" + cursorLikedAt + "&cursor_comment_id=" + cursorLikerId
          + "&limit="
          + limit;

    }
    CommonResponse<GetPostCommentListApiResponse> response = sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });

    return response;
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
   * 게시글 댓글 삭제 요청 전송
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

  /**
   * 게시글 댓글 좋아요 목록 커서 기반 조회 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   */
  public CommonResponse<GetPostCommentLikersApiResponse> sendGetPostCommentLikersRequest(
      String accessToken,
      String postId, String commentId, LocalDateTime cursorLikedAt, String cursorLikerId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/comments/" + commentId + "/likes";

    if (cursorLikedAt != null && cursorLikerId != null && cursorLikedAt != null) {
      path += "?cursor_liked_at=" + cursorLikedAt + "&cursor_liker_id=" + cursorLikerId + "&limit="
          + limit;

    }
    CommonResponse<GetPostCommentLikersApiResponse> response = sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });

    return response;
  }

  /**
   * 게시글 댓글 좋아요 요청 전송
   */
  public CommonResponse<LikePostCommentApiResponse> sendLikePostCommentRequest(
      String accessToken,
      String postId, String commentId, ResultMatcher expectedStatus) throws Exception {
    return
        sendPostRequest(
            "",
            "/api/posts/" + postId + "/comments/" + commentId + "/likes",
            expectedStatus,
            new TypeReference<CommonResponse<LikePostCommentApiResponse>>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }

  /**
   * 게시글 댓글 좋아요 삭제 요청 전송
   *
   * @param accessToken
   * @param postId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<LikePostCommentApiResponse> sendDeletePostCommentLikeRequest(
      String accessToken,
      String postId, String commentId, ResultMatcher expectedStatus) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + accessToken);

    return
        sendDeleteRequest("",
            "/api/posts/" + postId + "/comments/" + commentId + "/likes",
            expectedStatus, new TypeReference<CommonResponse<LikePostCommentApiResponse>>() {
            }, headers);
  }

  public static final int ACTIVE_POST_COUNT = 143;
  public static final int DELETED_POST_COUNT = 38;
  public static final int BLOCKED_POST_COUNT = 15;
  public static final int ARCHIVED_POST_COUNT = 5;
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
      Map.of("userId", "usr5", "userEmail", "noode@test.com", "postId", "post48"),
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
      Map.of("commentId", "cmt1", "postId", "post1", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt3", "postId", "post2", "userId", "usr5", "userEmail",
          "noodle@test.com"),
      Map.of("commentId", "cmt5", "postId", "post3", "userId", "usr5", "userEmail",
          "noodle@test.com"),
      Map.of("commentId", "cmt6", "postId", "post3", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt7", "postId", "post3", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt10", "postId", "post3", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt11", "postId", "post4", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt12", "postId", "post6", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt13", "postId", "post6", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt15", "postId", "post6", "userId", "usr4", "userEmail",
          "sunny@test.com")
  );

  // BLOCKED 상태의 댓글
  public static final List<Map<String, String>> BLOCKED_COMMENTS = List.of(
      Map.of("commentId", "cmt2", "postId", "post1", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt4", "postId", "post3", "userId", "usr5", "userEmail",
          "noodle@test.com"),
      Map.of("commentId", "cmt9", "postId", "post3", "userId", "usr5", "userEmail",
          "noodle@test.com"),
      Map.of("commentId", "cmt16", "postId", "post6", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt24", "postId", "post8", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt27", "postId", "post8", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt35", "postId", "post9", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt39", "postId", "post10", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt45", "postId", "post11", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt54", "postId", "post12", "userId", "usr4", "userEmail",
          "sunny@test.com")
  );

  // DELETED 상태의 댓글
  public static final List<Map<String, String>> DELETED_COMMENTS = List.of(
      Map.of("commentId", "cmt8", "postId", "post3", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt14", "postId", "post6", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt20", "postId", "post7", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt22", "postId", "post8", "userId", "usr4", "userEmail",
          "sunny@test.com"),
      Map.of("commentId", "cmt36", "postId", "post9", "userId", "usr5", "userEmail",
          "noodle@test.com"),
      Map.of("commentId", "cmt37", "postId", "post9", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt47", "postId", "post11", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt55", "postId", "post12", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com"),
      Map.of("commentId", "cmt74", "postId", "post16", "userId", "usr1", "userEmail",
          "user_email_verified1@test.com"),
      Map.of("commentId", "cmt79", "postId", "post16", "userId", "usr2", "userEmail",
          "user_email_verified2@test.com")
  );

  // ACTIVE 상태 댓글 좋아요 데이터
  public static final List<Map<String, String>> ACTIVE_COMMENT_LIKES = List.of(
      Map.of("commentId", "cmt102", "postId", "post23", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt3", "postId", "post2", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt5", "postId", "post3", "userEmail", "sunny@test.com"),
      Map.of("commentId", "cmt6", "postId", "post3", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt10", "postId", "post3", "userEmail", "noodle@test.com")
  );

  // BLOCKED / DELETED 상태 댓글 좋아요 데이터
  public static final List<Map<String, String>> INACTIVE_COMMENT_LIKES = List.of(
      Map.of("commentId", "cmt2", "postId", "post1", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt4", "postId", "post3", "userEmail", "user_email_verified1@test.com"),
      Map.of("commentId", "cmt8", "postId", "post3", "userEmail", "user_email_verified2@test.com"),
      Map.of("commentId", "cmt14", "postId", "post6", "userEmail", "noodle@test.com"),
      Map.of("commentId", "cmt16", "postId", "post6", "userEmail", "user_email_verified1@test.com")
  );
  public static final List<String> POST_ID_WITH_NO_LIKES = List.of(
      "post1", "post2", "post3", "post4", "post5",
      "post6", "post7", "post8", "post9", "post10"
  );

  public static final Map<String, Long> POST_LIKES = Map.ofEntries(
      Map.entry("post10", 34L),
      Map.entry("post11", 11L),
      Map.entry("post12", 10L),
      Map.entry("post13", 10L),
      Map.entry("post14", 10L),
      Map.entry("post15", 11L),
      Map.entry("post16", 10L),
      Map.entry("post17", 10L),
      Map.entry("post18", 10L),
      Map.entry("post19", 10L),
      Map.entry("post20", 11L)
  );
  public static final int POST_WITH_COMMENTS = 35;
}