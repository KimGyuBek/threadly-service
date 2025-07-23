package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.user.profile.register.RegisterUserProfileCommand;
import com.threadly.user.profile.register.RegisterUserProfileUseCase;
import com.threadly.user.profile.register.UserProfileRegistrationApiResponse;
import com.threadly.user.register.RegisterUserCommand;
import com.threadly.user.register.RegisterUserUseCase;
import com.threadly.user.register.UserRegistrationApiResponse;
import com.threadly.user.request.ResiterUserProfileRequest;
import com.threadly.user.request.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final EmailVerificationUseCase emailVerificationUseCase;
  private final RegisterUserProfileUseCase registerUserProfileUseCase;

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
   * 사용자 프로필 초기 설정
   *
   * @param user
   * @param request
   * @return
   */
  @PostMapping("/profile")
  public ResponseEntity<UserProfileRegistrationApiResponse> setUserProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody ResiterUserProfileRequest request) {

//    URI location = URI.create("/api/user/" + user.getUserId());

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

//  @PatchMapping("/profile")
//  public ResponseEntity<UpdateUserProfileApiResponse> updateUserProfile(
//      @AuthenticationPrincipal JwtAuthenticationUser user,
//      @RequestBody UpdateUserProfileRequest request) {
//
//  }

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
