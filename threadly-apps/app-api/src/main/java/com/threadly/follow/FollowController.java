package com.threadly.follow;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.follow.command.FollowCommandUseCase;
import com.threadly.follow.command.dto.FollowRelationCommand;
import com.threadly.follow.command.dto.FollowUserApiResponse;
import com.threadly.follow.command.dto.HandleFollowRequestCommand;
import com.threadly.follow.query.FollowQueryUseCase;
import com.threadly.follow.query.dto.GetFollowRequestsApiResponse;
import com.threadly.follow.query.dto.GetFollowRequestsQuery;
import com.threadly.follow.query.dto.GetFollowersApiResponse;
import com.threadly.follow.query.dto.GetFollowersQuery;
import com.threadly.follow.query.dto.GetFollowingsApiResponse;
import com.threadly.follow.query.dto.GetFollowingsQuery;
import com.threadly.follow.request.FollowRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")

public class FollowController {

  private final FollowCommandUseCase followCommandUseCase;
  private final FollowQueryUseCase followQueryUseCas;

  /**
   * 사용자 팔로우 요청
   *
   * @param request
   * @param user
   * @return
   */
  @PostMapping()
  public ResponseEntity<FollowUserApiResponse> followUser(
      @RequestBody FollowRequest request,
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {
    return ResponseEntity.ok()
        .body(followCommandUseCase.followUser(request.toCommand(user.getUserId())));
  }

  /**
   * 팔로우 요청 수락
   *
   * @param user
   * @param followId
   * @return
   */
  @PatchMapping("/{followId}/approve")
  public ResponseEntity<Void> approveFollowRequest(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("followId") String followId
  ) {
    followCommandUseCase.approveFollowRequest(
        new HandleFollowRequestCommand(user.getUserId(), followId));
    return ResponseEntity.ok().build();
  }

  /**
   * 팔로우 요청 거절
   *
   * @param user
   * @param followId
   * @return
   */
  @DeleteMapping("/{followId}")
  public ResponseEntity<Void> rejectFollowRequest(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("followId") String followId
  ) {
    followCommandUseCase.rejectFollowRequest(
        new HandleFollowRequestCommand(user.getUserId(), followId));
    return ResponseEntity.ok().build();
  }


  /**
   * 팔로우 요청 목록 커서 기반 조회
   *
   * @param user
   * @param cursorFollowRequestedAt
   * @param cursorFollowId
   * @param limit
   * @return
   */
  @GetMapping("/requests")
  public ResponseEntity<GetFollowRequestsApiResponse> getFollowRequests(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "cursor_follow_requested_at", required = false) LocalDateTime cursorFollowRequestedAt,
      @RequestParam(value = "cursor_follow_id", required = false) String cursorFollowId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    return ResponseEntity.status(200).body(followQueryUseCas.getFollowRequestsByCursor(
        new GetFollowRequestsQuery(user.getUserId(), cursorFollowRequestedAt, cursorFollowId,
            limit)));
  }

  /**
   * userId에 해당하는 사용자의 팔로워 목록 커서 기반 조회
   * <p>
   * userId가 없는 경우 인증 객체에서 userId 추출 후 내 팔로워 목록 조회
   * </p>
   *
   * @param user
   * @param cursorFollowedAt
   * @param cursorFollowerId
   * @param limit
   * @return
   */
  @GetMapping("/followers")
  public ResponseEntity<GetFollowersApiResponse> getFollowers(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "user_id", required = false) String targetUserId,
      @RequestParam(value = "cursor_followed_at", required = false) LocalDateTime cursorFollowedAt,
      @RequestParam(value = "cursor_follower_id", required = false) String cursorFollowerId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(followQueryUseCas.getFollowers(
            new GetFollowersQuery(user.getUserId(),
                (targetUserId != null) ? targetUserId : user.getUserId(),
                cursorFollowedAt, cursorFollowerId, limit
            )
        )
    );
  }

  /**
   * userId에 해당하는 사용자의 팔로잉 목록 커서 기반 조회
   * <p>
   * userId가 없는 경우 인증 객체에서 userId 추출 후 내 팔로잉 목록 조회
   * </p>
   *
   * @param user
   * @param cursorFollowedAt
   * @param cursorFollowingId
   * @param limit
   * @return
   */
  @GetMapping("/followings")
  public ResponseEntity<GetFollowingsApiResponse> getFollowings(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "user_id", required = false) String targetUserId,
      @RequestParam(value = "cursor_followed_at", required = false) LocalDateTime cursorFollowedAt,
      @RequestParam(value = "cursor_following_id", required = false) String cursorFollowingId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.status(200).body(followQueryUseCas.getFollowings(
            new GetFollowingsQuery(user.getUserId(),
                (targetUserId != null) ? targetUserId : user.getUserId(),
                cursorFollowedAt, cursorFollowingId, limit
            )
        )
    );
  }

  /**
   * 주어진 followId에 해당하는 팔로우 요청 삭제
   *
   * @param targetUserId
   * @param user
   * @return
   */
  @DeleteMapping("/requests/{targetUserId}")
  public ResponseEntity<Void> cancelFollowRequest(@PathVariable("targetUserId") String targetUserId,
      @AuthenticationPrincipal JwtAuthenticationUser user) {
    followCommandUseCase.cancelFollowRequest(
        new FollowRelationCommand(user.getUserId(), targetUserId));
    return ResponseEntity.status(200).build();
  }

  /**
   * 사용자 언팔로우
   *
   * @param followingUserId *
   * @param user
   * @return
   */
  @DeleteMapping("/following/{followingUserId}")
  public ResponseEntity<Void> unfollowUser(@PathVariable("followingUserId") String followingUserId,
      @AuthenticationPrincipal JwtAuthenticationUser user) {
    followCommandUseCase.unfollowUser(new FollowRelationCommand(user.getUserId(), followingUserId));
    return ResponseEntity.status(200).build();
  }


  /**
   * 팔로워 삭제
   *
   * @param followerId
   * @param user
   * @return
   */
  @DeleteMapping("/followers/{followerId}")
  public ResponseEntity<Void> removeFollower(@PathVariable("followerId") String followerId,
      @AuthenticationPrincipal JwtAuthenticationUser user) {
    followCommandUseCase.removeFollower(new FollowRelationCommand(user.getUserId(), followerId));
    return ResponseEntity.status(200).build();
  }

}
