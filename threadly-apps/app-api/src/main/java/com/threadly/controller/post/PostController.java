package com.threadly.controller.post;

import com.threadly.controller.post.request.CreatePostCommentRequest;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.controller.post.request.UpdatePostRequest;
import com.threadly.post.comment.create.CreatePostCommentApiResponse;
import com.threadly.post.comment.create.CreatePostCommentCommand;
import com.threadly.post.comment.create.CreatePostCommentUseCase;
import com.threadly.post.comment.delete.DeletePostCommentCommand;
import com.threadly.post.comment.delete.DeletePostCommentUseCase;
import com.threadly.post.comment.get.GetPostCommentListApiResponse;
import com.threadly.post.comment.get.GetPostCommentListQuery;
import com.threadly.post.comment.get.GetPostCommentUseCase;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.create.CreatePostCommand;
import com.threadly.post.create.CreatePostUseCase;
import com.threadly.post.delete.DeletePostCommand;
import com.threadly.post.delete.DeletePostUseCase;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import com.threadly.post.engagement.GetPostEngagementQuery;
import com.threadly.post.engagement.GetPostEngagementUseCase;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.post.get.GetPostListQuery;
import com.threadly.post.get.GetPostQuery;
import com.threadly.post.get.GetPostUseCase;
import com.threadly.post.like.comment.GetPostCommentLikersApiResponse;
import com.threadly.post.like.comment.GetPostCommentLikersQuery;
import com.threadly.post.like.comment.GetPostCommentLikersUseCase;
import com.threadly.post.like.comment.LikePostCommentApiResponse;
import com.threadly.post.like.comment.LikePostCommentCommand;
import com.threadly.post.like.comment.LikePostCommentUseCase;
import com.threadly.post.like.comment.UnlikePostCommentUseCase;
import com.threadly.post.like.post.GetPostLikersApiResponse;
import com.threadly.post.like.post.GetPostLikersQuery;
import com.threadly.post.like.post.GetPostLikersUseCase;
import com.threadly.post.like.post.LikePostApiResponse;
import com.threadly.post.like.post.LikePostCommand;
import com.threadly.post.like.post.LikePostUseCase;
import com.threadly.post.like.post.UnlikePostUseCase;
import com.threadly.post.update.UpdatePostApiResponse;
import com.threadly.post.update.UpdatePostCommand;
import com.threadly.post.update.UpdatePostUseCase;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*TODO 게시글과 댓글 컨트롤러 분리 고려*/

/**
 * post controller
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final CreatePostUseCase createPostUseCase;
  private final UpdatePostUseCase updatePostUseCase;
  private final GetPostUseCase getPostUseCase;
  private final LikePostUseCase likePostUseCase;
  private final UnlikePostUseCase unlikePostUseCase;
  private final GetPostEngagementUseCase getPostEngagementUsecase;
  private final GetPostLikersUseCase getPostLikersUseCase;
  private final DeletePostUseCase deletePostUseCase;

  private final CreatePostCommentUseCase createPostCommentUseCase;
  private final DeletePostCommentUseCase deletePostCommentUseCase;
  private final GetPostCommentUseCase getPostCommentUseCase;
  private final GetPostCommentLikersUseCase getPostCommentLikersUseCase;
//  private final GetPostCommentEngagementUseCase getPostCommentEngagementUseCase;


  private final LikePostCommentUseCase likePostCommentUseCase;
  private final UnlikePostCommentUseCase unlikePostCommentUseCase;

  /*
   * 게시글 저장 - POST /api/posts
   * 게시글 삭제 - DELETE /api/posts/{postId}
   * 게시글 조회 - GET /api/posts/{postId}
   * 게시글 목록 조회 - GET /api/posts
   * 게시글 수정 - PATCH /api/posts/{postId}
   * 게시글 통계 - GET /api/posts/{postId}/stats
   * 게시글 좋아요 - POST /api/posts/{postId}/likes
   * 게시글 활동 요약 조회 - GET /api/posts/{postId}/engagement
   * 게시글 활동 좋아요 목록조회 - GET /api/posts/{postId}/engagement/likes
   * 게시글 댓글 생성 - POST /api/posts/{postId}/comments
   * 게시글 댓글 삭제 - DELETE /api/posts/{postId}/comments
   * 게시글 댓글 목록 조회 - GET /api/posts/{postId}/comments
   * 게시글 댓글 좋아요 - POST /api/posts/{postId}/comments/{commentId}/likes
   * 게시글 댓글 좋아요 취소 - DELETE /api/posts/{postId}/comments/{commentId}/likes
   * 게시글 댓글 좋아요 목록 조회 - GET /api/posts/{postId}/comments/{commentsId}/likes
   */

  /**
   * postId로 게시글 조회
   *
   * @param postId
   * @return
   */
  @GetMapping("/{postId}")
  public ResponseEntity<GetPostDetailApiResponse> getPost(@PathVariable String postId) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(getPostUseCase.getPost(
        new GetPostQuery(postId, userId)
    ));
  }


  /**
   * 사용자에게 노출되는 게시글 목록을 커서 기반으로 조회
   * <p>
   * 최신 게시글 부터 수정일(postedAt) 기준으로 내림차순 정렬되며, 커서 값보다 이전에 수정된 게시글들을 조회
   *
   * @return
   */
  @GetMapping("")
  public ResponseEntity<GetPostDetailListApiResponse> getPostList(
      @RequestParam(value = "cursor_posted_at", required = false) LocalDateTime cursorPostedAt,
      @RequestParam(value = "cursor_post_id", required = false) String cursorPostId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        getPostUseCase.getUserVisiblePostListByCursor(
            new GetPostListQuery(
                userId, cursorPostedAt, cursorPostId, limit
            )
        )
    );
  }

  /**
   * 게시글 좋아요 요약 조회
   *
   * @param postId
   * @return
   */
  @GetMapping("/{postId}/engagement")
  public ResponseEntity<GetPostEngagementApiResponse> getPostEngagement(
      @PathVariable("postId") String postId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(getPostEngagementUsecase.getPostEngagement(
        new GetPostEngagementQuery(
            postId, userId
        )
    ));
  }


  /**
   * 특정 게시글 좋아요 누른 사용자 커서기반 조회
   *
   * @return
   */
  @GetMapping("/{postId}/engagement/likes")
  public ResponseEntity<GetPostLikersApiResponse> getPostLikers(
      @RequestParam(value = "cursor_liked_at", required = false) LocalDateTime cursorLikedAt,
      @RequestParam(value = "cursor_liker_id", required = false) String cursorLikerId,
      @RequestParam(value = "limit", defaultValue = "10") int limit,
      @PathVariable("postId") String postId
  ) {

    return ResponseEntity.status(200).body(getPostLikersUseCase.getPostLikers(
        new GetPostLikersQuery(
            postId, cursorLikedAt, cursorLikerId, limit
        )
    ));
  }

  /**
   * 게시글 생성
   *
   * @return
   */
  @PostMapping("")
  public ResponseEntity<CreatePostApiResponse> createPost(
      @Valid @RequestBody CreatePostRequest request) {
    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(201).body(
        createPostUseCase.createPost(
            new CreatePostCommand(userId, request.content())
        )
    );
  }

  /**
   * 게시글 수정
   *
   * @param request
   * @param postId
   * @return
   */
  @PatchMapping("/{postId}")
  public ResponseEntity<UpdatePostApiResponse> updatePost(
      @Valid @RequestBody UpdatePostRequest request,
      @PathVariable("postId") String postId) {

    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        updatePostUseCase.updatePost(
            new UpdatePostCommand(postId, userId, request.content())
        )
    );
  }

  /**
   * 게시글 삭제 상태로 변경
   *
   * @param postId
   * @return
   */
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable("postId") String postId) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    deletePostUseCase.softDeletePost(
        new DeletePostCommand(postId, userId)
    );

    return ResponseEntity.status(200).build();
  }

  /**
   * 게시글 좋아요
   *
   * @param postId
   * @return
   */
  @PostMapping("/{postId}/likes")
  public ResponseEntity<LikePostApiResponse> likePost(@PathVariable("postId") String postId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        likePostUseCase.likePost(
            new LikePostCommand(
                postId,
                userId
            )
        )
    );
  }

  /**
   * 게시글 좋아요 취소
   *
   * @param postId
   * @return
   */
  @DeleteMapping("/{postId}/likes")
  public ResponseEntity<LikePostApiResponse> cancelPostLike(@PathVariable("postId") String postId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(204).body(unlikePostUseCase.cancelLikePost(
        new LikePostCommand(
            postId,
            userId
        )
    ));
  }

//  @GetMapping("/{postId}/comments/{commentId}/engagement")
//  public ResponseEntity<GetPostCommentEngagementApiResponse> getPostCommentEngagement() {
//
//    return null;
//  }

  /**
   * 게시글 댓글 목록 커서 기반 조회
   *
   * @param postId
   * @param cursorCommentedAt
   * @param cursorCommentId
   * @param limit
   * @return
   */
  @GetMapping("/{postId}/comments")
  public ResponseEntity<GetPostCommentListApiResponse> getPostComments(@PathVariable String postId,
      @RequestParam(value = "cursor_commented_at", required = false) LocalDateTime cursorCommentedAt,
      @RequestParam(value = "cursor_comment_id", required = false) String cursorCommentId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(200).body(
        getPostCommentUseCase.getPostCommentDetailListForUser(
            new GetPostCommentListQuery(
                postId,
                userId,
                cursorCommentedAt,
                cursorCommentId,
                limit
            )
        )
    );
  }


  /**
   * 게시글 댓글 생성
   *
   * @param postId
   * @param request
   * @return createPostCommentApiResponse
   */
  @PostMapping("/{postId}/comments")
  public ResponseEntity<CreatePostCommentApiResponse> createPostComment(
      @PathVariable("postId") String postId, @RequestBody CreatePostCommentRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(201).body(
        createPostCommentUseCase.createPostComment(
            new CreatePostCommentCommand(
                postId,
                userId,
                request.content()
            )
        )
    );
  }

  /**
   * 게시글 댓글 삭제
   *
   * @param commentId
   * @return
   */
  @DeleteMapping("/{postId}/comments/{commentId}")
  public ResponseEntity<Void> deletePostComment(@PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    deletePostCommentUseCase.softDeletePostComment(
        new DeletePostCommentCommand(
            userId,
            postId,
            commentId)
    );

    return ResponseEntity.status(204).build();
  }


  /**
   * 게시글 댓글 좋아요 목록 조회
   *
   * @param postId
   * @param commentId
   * @param cursorLikedAt
   * @param cursorLikerId
   * @return
   */
  @GetMapping("/{postId}/comments/{commentId}/likes")
  public ResponseEntity<GetPostCommentLikersApiResponse> getPostCommentLikers(
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId,
      @RequestParam(value = "cursor_liked_at", required = false) LocalDateTime cursorLikedAt,
      @RequestParam(value = "cursor_liker_id", required = false) String cursorLikerId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(getPostCommentLikersUseCase.getPostCommentLikers(
            new GetPostCommentLikersQuery(
                postId, commentId, cursorLikedAt, cursorLikerId, limit)
        )
    );
  }


  /**
   * 게시글 댓글 좋아요
   *
   * @param postId
   * @param commentId
   * @return
   */
  @PostMapping("/{postId}/comments/{commentId}/likes")
  public ResponseEntity<LikePostCommentApiResponse> likePostComment(
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId
  ) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    LikePostCommentApiResponse likePostCommentApiResponse = likePostCommentUseCase.likePostComment(
        new LikePostCommentCommand(
            commentId,
            userId
        )
    );

    return ResponseEntity.status(200).body(likePostCommentApiResponse);
  }

  /**
   * 게시글 댓글 좋아요 삭제
   *
   * @return
   */
  @DeleteMapping("/{postId}/comments/{commentId}/likes")
  public ResponseEntity<LikePostCommentApiResponse> cancelPostCommentLike(
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return ResponseEntity.status(204).body(
        unlikePostCommentUseCase.cancelPostCommentLike(
            new LikePostCommentCommand(
                commentId,
                userId
            )
        )
    );
  }


}
