package com.threadly.post.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 좋아요 API", description = "게시글 좋아요 관련 API")
public interface PostLikeApi {

  @Operation(summary = "게시글 활동 조회")
  @GetMapping("/{postId}/engagement")
  ResponseEntity<GetPostEngagementApiResponse> getPostEngagement(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId);

  @Operation(summary = "게시글 좋아요한 사용자 목록 조회")
  @GetMapping("/{postId}/likers")
  ResponseEntity<CursorPageApiResponse> getPostLikers(
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit,
      @PathVariable("postId") String postId);

  @Operation(summary = "게시글 좋아요")
  @PostMapping("/{postId}/like")
  ResponseEntity<LikePostApiResponse> likePost(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId);

  @Operation(summary = "게시글 좋아요 취소")
  @DeleteMapping("/{postId}/like")
  ResponseEntity<LikePostApiResponse> cancelPostLike(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("postId") String postId);

}
