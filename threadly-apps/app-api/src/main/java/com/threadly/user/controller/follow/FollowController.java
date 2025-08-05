package com.threadly.user.controller.follow;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.user.follow.FollowUserApiResponse;
import com.threadly.user.follow.FollowUserUseCase;
import com.threadly.user.follow.get.FollowQueryUseCase;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse;
import com.threadly.user.follow.get.GetFollowRequestsQuery;
import com.threadly.user.follow.get.GetFollowersApiResponse;
import com.threadly.user.follow.get.GetFollowersQuery;
import com.threadly.user.follow.get.GetFollowingsApiResponse;
import com.threadly.user.follow.get.GetFollowingsQuery;
import com.threadly.user.request.follow.FollowRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")

public class FollowController {

  private final FollowUserUseCase followUserUseCase;
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
        .body(followUserUseCase.followUser(request.toCommand(user.getUserId())));
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
            new GetFollowersQuery((targetUserId != null) ? targetUserId : user.getUserId(),
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
            new GetFollowingsQuery((targetUserId != null) ? targetUserId : user.getUserId(),
                cursorFollowedAt, cursorFollowingId, limit
            )
        )
    );
  }
}
