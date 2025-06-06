package com.threadly.post.controller;

import com.threadly.post.like.comment.GetPostCommentLikersApiResponse;
import com.threadly.post.like.comment.GetPostCommentLikersQuery;
import com.threadly.post.like.comment.GetPostCommentLikersUseCase;
import com.threadly.post.like.comment.LikePostCommentApiResponse;
import com.threadly.post.like.comment.LikePostCommentCommand;
import com.threadly.post.like.comment.LikePostCommentUseCase;
import com.threadly.post.like.comment.UnlikePostCommentUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 게시글 댓글 좋아요, 취소, 조회 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostCommentLikeController {

  private final GetPostCommentLikersUseCase getPostCommentLikersUseCase;
  private final LikePostCommentUseCase likePostCommentUseCase;
  private final UnlikePostCommentUseCase unlikePostCommentUseCase;

  /*
   * 게시글 댓글 좋아요 - POST /api/posts/{postId}/comments/{commentId}/likes
   * 게시글 댓글 좋아요 취소 - DELETE /api/posts/{postId}/comments/{commentId}/likes
   * 게시글 댓글 좋아요 목록 조회 - GET /api/posts/{postId}/comments/{commentsId}/likes
   */

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
