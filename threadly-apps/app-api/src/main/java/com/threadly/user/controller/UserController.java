package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.user.profile.get.GetMyProfileEditApiResponse;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
import com.threadly.user.profile.get.GetUserProfileUseCase;
import com.threadly.user.profile.register.RegisterUserProfileCommand;
import com.threadly.user.profile.register.RegisterUserProfileUseCase;
import com.threadly.user.profile.register.UserProfileRegistrationApiResponse;
import com.threadly.user.profile.update.UpdateUserProfileUseCase;
import com.threadly.user.register.RegisterUserCommand;
import com.threadly.user.register.RegisterUserUseCase;
import com.threadly.user.register.UserRegistrationApiResponse;
import com.threadly.user.request.RegisterUserProfileRequest;
import com.threadly.user.request.UpdateUserProfileRequest;
import com.threadly.user.request.UserRegisterRequest;
import jakarta.validation.Valid;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final EmailVerificationUseCase emailVerificationUseCase;
  private final RegisterUserProfileUseCase registerUserProfileUseCase;

  private final UpdateUserProfileUseCase updateUserProfileUseCase;
  private final GetUserProfileUseCase getUserProfileUseCase;

  private final ReissueTokenUseCase reissueTokenUseCase;

  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("")
  public UserRegistrationApiResponse register(
      @Valid @RequestBody UserRegisterRequest request
  ) {

    /*회원 가입*/
    UserRegistrationApiResponse response = registerUserUseCase.register(
        RegisterUserCommand.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build()
    );

    /*인증 메일 전송*/
    emailVerificationUseCase.sendVerificationEmail(response.getUserId());

    return response;
  }

  /**
   * 내 프로필 수정 용 정보 조회
   *
   * @return
   */
  @GetMapping("/profile/edit")
  public ResponseEntity<GetMyProfileEditApiResponse> getMyProfileEdit() {

    return ResponseEntity.status(200).body(null);
  }

//  /**
//   * 사용자 프로필 조회
//   *
//   * @return
//   */
//  @GetMapping("/profile")
//  public ResponseEntity<GetMyProfileApiResponse> getMyProfile(
//      @AuthenticationPrincipal JwtAuthenticationUser user
//  ) {
//
//    return ResponseEntity.status(200).body(null);
//  }

  /**
   * 사용자 프로필 조회
   *
   * @return
   */
  @GetMapping("/profile/{userId}")
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
  @PostMapping("/profile")
  public ResponseEntity<UserProfileRegistrationApiResponse> setUserProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody RegisterUserProfileRequest request) {

    /*프로필 설정*/
    registerUserProfileUseCase.registerUserProfile(
        new RegisterUserProfileCommand(
            user.getUserId(),
            request.getNickname(),
            request.getStatusMessage(),
            request.getBio(),
            request.getPhone(),
            request.getGender(),
            request.getProfileImageUrl())
    );
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
  @PatchMapping("/profile")
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
  @GetMapping("/profile/check")
  public ResponseEntity<Void> checkNickname(@RequestParam("nickname") String nickName) {
    getUserProfileUseCase.validateNicknameUnique(nickName);
    return ResponseEntity.status(200).build();
  }


  /**
   * 사용자 비밀번호 변경
   *
   * @return
   */
  @PatchMapping("/profile/password")
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
