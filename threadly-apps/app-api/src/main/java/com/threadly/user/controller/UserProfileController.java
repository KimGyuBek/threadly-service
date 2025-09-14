package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
import com.threadly.core.port.user.in.profile.query.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user profile controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/profile")
public class UserProfileController {

  private final GetUserProfileUseCase getUserProfileUseCase;


  /**
   * 사용자 프로필 조회
   *
   * @return
   */
  @GetMapping("/{userId}")
  public ResponseEntity<GetUserProfileApiResponse> getOtherUserProfile(@AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable("userId") String targetUserId) {

    return ResponseEntity.status(200).body(getUserProfileUseCase.getUserProfile(user.getUserId(), targetUserId));
  }


}
