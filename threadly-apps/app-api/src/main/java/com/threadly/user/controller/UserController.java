package com.threadly.user.controller;

import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.user.account.command.dto.RegisterUserApiResponse;
import com.threadly.user.account.command.RegisterUserUseCase;
import com.threadly.user.request.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final EmailVerificationUseCase emailVerificationUseCase;


  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("")
  public RegisterUserApiResponse register(
      @Valid @RequestBody UserRegisterRequest request
  ) {
    /*회원 가입*/
    RegisterUserApiResponse response = registerUserUseCase.register(request.toCommand());

    /*인증 메일 전송*/
    emailVerificationUseCase.sendVerificationEmail(response.getUserId());

    return response;
  }
}
