package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.LoginAuthenticationUser;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.auth.verification.LoginUserUseCase;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.UpdateUserUseCase;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.command.UserSetProfileCommand;
import com.threadly.user.request.CreateUserProfileRequest;
import com.threadly.user.request.UserRegisterRequest;
import com.threadly.user.response.UserProfileSetupApiResponse;
import com.threadly.user.response.UserRegistrationResponse;
import jakarta.validation.Valid;
import java.net.URI;
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
  private final UpdateUserUseCase updateUserUseCase;

  private final ReissueTokenUseCase reissueTokenUseCase;

  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("")
  public UserRegistrationResponse register(
      @Valid @RequestBody UserRegisterRequest request
  ) {

    /*회원 가입*/
    UserRegistrationResponse response = registerUserUseCase.register(
        UserRegistrationCommand.builder()
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
  public ResponseEntity<UserProfileSetupApiResponse> setUserProfile(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody CreateUserProfileRequest request) {

    URI location = URI.create("/api/user/" + user.getUserId());

    /*프로필 설정*/
    updateUserUseCase.upsertUserProfile(
        new UserSetProfileCommand(
            user.getUserId(),
            request.getNickname(),
            request.getStatusMessage(),
            request.getBio(),
            request.getGender(),
            request.getProfileImageUrl())
    );
    return ResponseEntity.status(200).body(
        reissueTokenUseCase.reissueToken(user.getUserId())
    );

//    return ResponseEntity.created(location)
//        .body(updateUserUseCase.upsertUserProfile(new UserSetProfileCommand(
//                user.getUserId(),
//                request.getNickname(),
//                request.getStatusMessage(),
//                request.getBio(),
//                request.getGender(),
//                request.getProfileImageUrl())
//            )
//        );
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
