package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.user.profile.get.GetMyProfileEditApiResponse;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
import com.threadly.user.profile.get.GetUserProfileUseCase;
import com.threadly.user.profile.register.RegisterUserProfileUseCase;
import com.threadly.user.profile.register.UserProfileRegistrationApiResponse;
import com.threadly.user.profile.update.UpdateUserProfileUseCase;
import com.threadly.user.request.RegisterUserProfileRequest;
import com.threadly.user.request.UpdateUserProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * user profile controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

  private final RegisterUserProfileUseCase registerUserProfileUseCase;
  private final UpdateUserProfileUseCase updateUserProfileUseCase;
  private final GetUserProfileUseCase getUserProfileUseCase;

  private final ReissueTokenUseCase reissueTokenUseCase;

  /**
   * 내 프로필 수정용 정보 조회
   *
   * @return
   */
  @GetMapping("/edit")
  public ResponseEntity<GetMyProfileEditApiResponse> getMyProfileEdit() {

    return ResponseEntity.status(200).body(null);
  }

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

  /**
   * 사용자 프로필 초기 설정
   *
   * @param user
   * @param request
   * @return
   */
  @PostMapping("")
  public ResponseEntity<UserProfileRegistrationApiResponse> setUserProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody RegisterUserProfileRequest request) {

    /*프로필 설정*/
    registerUserProfileUseCase.registerUserProfile(request.toCommand(user.getUserId()));

    return ResponseEntity.status(201).body(
        reissueTokenUseCase.reissueToken(user.getUserId())
    );
  }

  /**
   * 사용자 프로필 업데이트
   *
   * @param user
   * @param request
   * @return
   */
  @PatchMapping("")
  public ResponseEntity<Void> updateUserProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody UpdateUserProfileRequest request) {

    updateUserProfileUseCase.updateUserProfile(request.toCommand(user.getUserId()));
    return ResponseEntity.status(200).build();
  }

  /**
   * nickname 중복 체크
   *
   * @param nickName
   * @return
   */
  @GetMapping("/check")
  public ResponseEntity<Void> checkNickname(@RequestParam("nickname") String nickName) {
    getUserProfileUseCase.validateNicknameUnique(nickName);
    return ResponseEntity.status(200).build();
  }


  /**
   * 사용자 비밀번호 변경
   *
   * @return
   */
  @PatchMapping("/password")
  public ResponseEntity<Void> changePassword() {

    return ResponseEntity.noContent().build();
  }

  /**
   * test - 사용자 정보 비밀번호 수정
   *
   * @return
   */
  @PostMapping("/update/password")
  public boolean updatePassword() {
    return true;

  }


}
