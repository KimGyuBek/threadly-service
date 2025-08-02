package com.threadly.user.controller.follow;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.user.follow.FollowUserApiResponse;
import com.threadly.user.follow.FollowUserUseCase;
import com.threadly.user.request.follow.FollowRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")

public class FollowController {

  private final FollowUserUseCase followUserUseCase;

  /**
   * 사용자 팔로우 요청
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

}
