package com.threadly.post.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.usecase.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.core.usecase.post.comment.get.GetPostCommentApiResponse;
import com.threadly.core.usecase.post.create.CreatePostApiResponse;
import com.threadly.core.usecase.post.engagement.GetPostEngagementApiResponse;
import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.core.usecase.post.get.PostDetails;
import com.threadly.core.usecase.post.like.comment.LikePostCommentApiResponse;
import com.threadly.core.usecase.post.like.comment.PostCommentLiker;
import com.threadly.core.usecase.post.like.post.LikePostApiResponse;
import com.threadly.core.usecase.post.like.post.PostLiker;
import com.threadly.adapter.persistence.post.repository.PostJpaRepository;
import com.threadly.post.request.CreatePostCommentRequest;
import com.threadly.post.request.CreatePostRequest;
import com.threadly.post.request.UpdatePostRequest;
import com.threadly.core.usecase.post.update.UpdatePostApiResponse;
import com.threadly.commons.response.CursorPageApiResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Base Post Api Test
 */
public abstract class BasePostApiTest extends BaseApiTest {

  @Autowired
  private PostJpaRepository postJpaRepository;

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
  public CommonResponse<PostDetails> sendGetPostRequest(String accessToken,
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
  public CommonResponse<CursorPageApiResponse<PostLiker>> sendGetPostLikersRequest(
      String accessToken,
      String postId, LocalDateTime cursorTimestamp, String cursorLikerId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/engagement/likes";

    if (cursorTimestamp != null && cursorLikerId != null && cursorTimestamp != null) {
      path += "?cursor_timestamp=" + cursorTimestamp + "&cursor_id=" + cursorLikerId + "&limit="
          + limit;

    }
    return sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });
  }

  /**
   * 게시글 목록 조회 요청 전송
   *
   * @param accessToken
   * @param expectedStatus
   * @return
   */
  public CommonResponse<CursorPageApiResponse<PostDetails>> sendGetPostListRequest(
      String accessToken,
      LocalDateTime cursorTimestamp, String cursorId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path =
        "/api/posts?limit=" + limit;

    if (cursorTimestamp != null || cursorId != null) {
      path += "&cursor_timestamp=" + cursorTimestamp + "&cursor_id=" + cursorId;
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
  public CommonResponse<CursorPageApiResponse<GetPostCommentApiResponse>> sendGetPostCommentListRequest(
      String accessToken,
      String postId, LocalDateTime cursorTimestamp, String cursorId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/comments";

    if (cursorTimestamp != null && cursorId != null && cursorTimestamp != null) {
      path += "?cursor_timestamp=" + cursorTimestamp + "&cursor_id=" + cursorId
          + "&limit="
          + limit;

    }
    return sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });
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
  public CommonResponse<CursorPageApiResponse<PostCommentLiker>> sendGetPostCommentLikersRequest(
      String accessToken,
      String postId, String commentId, LocalDateTime cursorTimestamp, String cursorId, int limit,
      ResultMatcher expectedStatus) throws Exception {
    String path = "/api/posts/" + postId + "/comments/" + commentId + "/likes";

    if (cursorTimestamp != null && cursorId != null && cursorTimestamp != null) {
      path += "?cursor_timestamp=" + cursorTimestamp + "&cursor_id=" + cursorId + "&limit="
          + limit;

    }
    return sendGetRequest(
        accessToken, path, expectedStatus,
        new TypeReference<>() {
        });
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

  /**
   * 게시글 이미지 상태 검증
   *
   * @param postId
   * @param expectedStatus
   */
  public void validatePostStatus(String postId, PostStatus expectedStatus) {
    PostEntity postEntity = postJpaRepository.findById(postId).get();
    assertThat(postEntity.getStatus()).isEqualTo(expectedStatus);
  }
}