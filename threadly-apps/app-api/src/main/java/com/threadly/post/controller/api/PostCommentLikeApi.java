package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentApiResponse;
import com.threadly.core.port.post.in.like.comment.query.dto.PostCommentLiker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 좋아요 API", description = "댓글 좋아요 관련 API")
public interface PostCommentLikeApi {

  @Operation(summary = "댓글 좋아요한 사용자 목록 조회")
  @GetMapping("/{postId}/comments/{commentId}/likers")
  ResponseEntity<CursorPageApiResponse<PostCommentLiker>> getPostCommentLikers(
      @PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "댓글 좋아요")
  @PostMapping("/{postId}/comments/{commentId}/like")
  ResponseEntity<LikePostCommentApiResponse> likePostComment(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId);

  @Operation(summary = "댓글 좋아요 취소")
  @DeleteMapping("/{postId}/comments/{commentId}/like")
  ResponseEntity<LikePostCommentApiResponse> cancelPostCommentLike(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId);

}
