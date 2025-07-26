package com.threadly.user.controller;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.user.register.RegisterUserUseCase;
import com.threadly.user.register.UserRegistrationApiResponse;
import com.threadly.user.request.UserRegisterRequest;
import com.threadly.user.withdraw.WithdrawUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final EmailVerificationUseCase emailVerificationUseCase;

  private final WithdrawUserUseCase withdrawUserUsecase;

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
    UserRegistrationApiResponse response = registerUserUseCase.register(request.toCommand());

    /*인증 메일 전송*/
    emailVerificationUseCase.sendVerificationEmail(response.getUserId());

    return response;
  }

  /**
   * 사용자 탈퇴
   *
   * @return
   */
  @DeleteMapping("/me")
  public ResponseEntity<Void> withdrawUser(@RequestHeader("Authorization") String bearerToken,
      @AuthenticationPrincipal JwtAuthenticationUser user) {
    withdrawUserUsecase.withdrawUser(user.getUserId(), bearerToken);

    return ResponseEntity.status(200).build();
  }


}
