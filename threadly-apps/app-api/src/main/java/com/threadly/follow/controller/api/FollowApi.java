package com.threadly.follow.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.follow.in.command.dto.FollowUserApiResponse;
import com.threadly.core.port.follow.in.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.follow.request.FollowRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팔로우 API", description = "팔로우/언팔로우 및 관계 관리 API")
public interface FollowApi {

  @Operation(summary = "사용자 팔로우", description = "다른 사용자를 팔로우합니다. 비공개 계정의 경우 팔로우 요청이 전송됩니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로우 성공 또는 팔로우 요청 전송",
          content = @Content(schema = @Schema(implementation = FollowUserApiResponse.class)))
  })
  @PostMapping()
  ResponseEntity<FollowUserApiResponse> followUser(
      @RequestBody FollowRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  @Operation(summary = "팔로우 요청 승인")
  @PatchMapping("/{followId}/approve")
  ResponseEntity<Void> approveFollowRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("followId") String followId);

  @Operation(summary = "팔로우 요청 거절")
  @DeleteMapping("/{followId}")
  ResponseEntity<Void> rejectFollowRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("followId") String followId);

  @Operation(summary = "팔로우 요청 목록 조회")
  @GetMapping("/requests")
  ResponseEntity<CursorPageApiResponse> getFollowRequests(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "팔로워 목록 조회")
  @GetMapping("/followers")
  ResponseEntity<CursorPageApiResponse> getFollowers(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "user_id", required = false) String targetUserId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "팔로잉 목록 조회")
  @GetMapping("/followings")
  ResponseEntity<CursorPageApiResponse> getFollowings(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "user_id", required = false) String targetUserId,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  @Operation(summary = "팔로우 요청 취소")
  @DeleteMapping("/requests/{targetUserId}")
  ResponseEntity<Void> cancelFollowRequest(
      @PathVariable("targetUserId") String targetUserId,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  @Operation(summary = "언팔로우")
  @DeleteMapping("/following/{followingUserId}")
  ResponseEntity<Void> unfollowUser(
      @PathVariable("followingUserId") String followingUserId,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  @Operation(summary = "팔로워 삭제")
  @DeleteMapping("/followers/{followerId}")
  ResponseEntity<Void> removeFollower(
      @PathVariable("followerId") String followerId,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  @Operation(summary = "팔로우 통계 조회", description = "특정 사용자의 팔로워/팔로잉 수를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = GetUserFollowStatsApiResponse.class)))
  })
  @GetMapping("/{userId}/stats")
  ResponseEntity<GetUserFollowStatsApiResponse> getUserFollowStats(
      @PathVariable("userId") String userId);

}
