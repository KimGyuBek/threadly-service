package com.threadly.auth.controller;

import com.threadly.auth.controller.api.AuthViewApi;
import com.threadly.core.port.auth.in.verification.EmailVerificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthViewController implements AuthViewApi {

  private final EmailVerificationUseCase emailVerificationUseCase;

  /**
   * 이메일 인증
   *
   * @param code
   */
  @GetMapping("/verify-email")
  public String verifyMail(@RequestParam String code) {

    emailVerificationUseCase.verifyEmail(code);

    return "auth/verify-email";
  }

}
