package com.threadly.adapter.persistence.core.post.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.usecase.post.like.comment.PostCommentLiker;
import com.threadly.core.usecase.post.like.comment.GetPostCommentLikersQuery;
import com.threadly.core.usecase.post.like.comment.GetPostCommentLikersUseCase;
import com.threadly.core.usecase.post.like.comment.LikePostCommentApiResponse;
import com.threadly.core.usecase.post.like.comment.LikePostCommentCommand;
import com.threadly.core.usecase.post.like.comment.LikePostCommentUseCase;
import com.threadly.core.usecase.post.like.comment.UnlikePostCommentUseCase;
import com.threadly.commons.response.CursorPageApiResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
   * @param cursorId
   * @param cursorTimestamp
   * @return
   */
  @GetMapping("/{postId}/comments/{commentId}/likes")
  public ResponseEntity<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikers(
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(getPostCommentLikersUseCase.getPostCommentLikers(
            new GetPostCommentLikersQuery(
                postId, commentId, cursorTimestamp, cursorId, limit)
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
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId
  ) {

    LikePostCommentApiResponse likePostCommentApiResponse = likePostCommentUseCase.likePostComment(
        new LikePostCommentCommand(
            commentId,
            user.getUserId()
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
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId, @PathVariable("commentId") String commentId) {

    return ResponseEntity.status(204).body(
        unlikePostCommentUseCase.cancelPostCommentLike(
            new LikePostCommentCommand(
                commentId,
                user.getUserId())));
  }
}
