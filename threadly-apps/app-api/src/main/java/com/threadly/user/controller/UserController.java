package com.threadly.user.controller;

import com.threadly.auth.AuthenticationUser;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.UpdateUserUseCase;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.command.UserSetProfileCommand;
import com.threadly.user.request.CreateUserProfileRequest;
import com.threadly.user.request.UserRegisterRequest;
import com.threadly.user.response.UserProfileApiResponse;
import com.threadly.user.response.UserRegistrationResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final EmailVerificationUseCase emailVerificationUseCase;
  private final UpdateUserUseCase updateUserUseCase;


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

  @PostMapping("/profile")
  public ResponseEntity<UserProfileApiResponse> setUserProfile(
      @AuthenticationPrincipal AuthenticationUser user,
      @RequestBody CreateUserProfileRequest request) {

    URI location = URI.create("/api/users/" + user.getUserId());

    return ResponseEntity.created(location)
        .body(updateUserUseCase.upsertUserProfile(new UserSetProfileCommand(
                user.getUserId(),
                request.getNickname(),
                request.getStatusMessage(),
                request.getBio(),
                request.getGender(),
                request.getProfileImageUrl())
            )
        );
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
