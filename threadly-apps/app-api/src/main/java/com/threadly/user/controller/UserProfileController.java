package com.threadly.user.controller;

import com.threadly.user.profile.get.GetUserProfileApiResponse;
import com.threadly.user.profile.get.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user profile controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

  private final GetUserProfileUseCase getUserProfileUseCase;


  /**
   * 사용자 프로필 조회
   *
   * @return
   */
  @GetMapping("/{userId}")
  public ResponseEntity<GetUserProfileApiResponse> getOtherUserProfile(
      @PathVariable String userId) {

    return ResponseEntity.status(200).body(getUserProfileUseCase.getUserProfile(userId));
  }


}
