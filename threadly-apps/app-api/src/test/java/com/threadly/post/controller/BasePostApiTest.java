package com.threadly.post.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
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
import com.threadly.post.request.CreatePostCommentRequest;
import com.threadly.post.request.CreatePostRequest;
import com.threadly.post.request.UpdatePostRequest;
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
      String content, List<CreatePostRequest.ImageRequest> images,
      ResultMatcher expectedStatus) throws Exception {

    String requestBody = generateRequestBody(new CreatePostRequest(content, images));
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
}