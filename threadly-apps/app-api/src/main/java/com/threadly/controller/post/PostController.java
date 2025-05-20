package com.threadly.controller.post;

import com.threadly.controller.post.request.CreatePostCommentRequest;
import com.threadly.controller.post.request.CreatePostRequest;
import com.threadly.controller.post.request.UpdatePostRequest;
import com.threadly.post.CreatePostUseCase;
import com.threadly.post.FetchPostUseCase;
import com.threadly.post.UpdatePostUseCase;
import com.threadly.post.command.CreatePostCommand;
import com.threadly.post.command.DeletePostCommand;
import com.threadly.post.command.UpdatePostCommand;
import com.threadly.post.comment.CreatePostCommentUseCase;
import com.threadly.post.comment.UpdatePostCommentUseCase;
import com.threadly.post.comment.command.CreatePostCommentCommand;
import com.threadly.post.comment.command.DeletePostCommentCommand;
import com.threadly.post.comment.like.CancelPostCommentLikeUseCase;
import com.threadly.post.comment.like.LikePostCommentUseCase;
import com.threadly.post.comment.like.command.LikePostCommentCommand;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;
import com.threadly.post.comment.response.CreatePostCommentApiResponse;
import com.threadly.post.response.CreatePostApiResponse;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.PostStatusApiResponse;
import com.threadly.post.response.UpdatePostApiResponse;
import jakarta.validation.Valid;
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
  private final FetchPostUseCase fetchPostUseCase;

  private final CreatePostCommentUseCase createPostCommentUseCase;
  private final UpdatePostCommentUseCase updatePostCommentUseCase;

  private final LikePostCommentUseCase likePostCommentUseCase;
  private final CancelPostCommentLikeUseCase cancelPostCommentLikeUseCase;

  /*
   * 게시글 저장 - POST /api/posts
   * 게시글 삭제 - DELETE /api/posts/{postId}
   * 게시글 조회 - GET /api/posts/{postId}
   * 게시글 목록 조회 - GET /api/posts
   * 게시글 수정 - PATCH /api/posts/{postId}
   * 게시글 통계 - GET /api/posts/{postId}/stats
   * 게시글 좋아요 - POST /api/posts/{postId}/likes
   * 게시글 좋아요 목록 조회 - GET /api/posts/{postId}/likes
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
  public ResponseEntity<PostDetailApiResponse> getPost(@PathVariable String postId) {
    return ResponseEntity.status(200).body(fetchPostUseCase.getPost(postId));
  }

  /**
   * 게시글 목록 조회
   *
   * @return
   */
  @GetMapping("")
  public ResponseEntity<PostDetailListApiResponse> getPostList() {
    return ResponseEntity.status(200).body(
        fetchPostUseCase.getUserVisiblePostList()
    );
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

    updatePostUseCase.deletePost(
        new DeletePostCommand(postId, userId)
    );

    return ResponseEntity.status(200).build();
  }

  /**
   * 게시글 좋아요/댓글 수 통계 조회
   *
   * @param postId
   * @return
   */
  @GetMapping("/{postId}/stats")
  public ResponseEntity<PostStatusApiResponse> getPostStatus(
      @PathVariable("postId") String postId) {

    return ResponseEntity.status(200).body(null);
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

    updatePostCommentUseCase.deletePostComment(
        new DeletePostCommentCommand(
            userId,
            postId,
            commentId)
    );

    return ResponseEntity.status(204).build();
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

    return ResponseEntity.status(201).body(likePostCommentApiResponse);
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
        cancelPostCommentLikeUseCase.cancelPostCommentLike(
            new LikePostCommentCommand(
                commentId,
                userId
            )
        )
    );
  }


}
