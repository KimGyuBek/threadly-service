package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentApiResponse;
import com.threadly.post.request.CreatePostCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 댓글 API", description = "게시글 댓글 관련 API")
public interface PostCommentApi {

  @Operation(summary = "댓글 목록 조회")
  @GetMapping("/{postId}/comments")
  ResponseEntity getPostComments(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String postId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "댓글 작성")
  @PostMapping("/{postId}/comments")
  ResponseEntity<CreatePostCommentApiResponse> createPostComment(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String postId,
      @RequestBody CreatePostCommentRequest request);

  @Operation(summary = "댓글 삭제")
  @DeleteMapping("/{postId}/comments/{commentId}")
  ResponseEntity<Void> deletePostComment(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId,
      @PathVariable("commentId") String commentId);

}
