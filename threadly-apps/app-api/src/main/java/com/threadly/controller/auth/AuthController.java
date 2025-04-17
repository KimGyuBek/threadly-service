package com.threadly.controller.auth;

import com.threadly.auth.AuthService;
import com.threadly.auth.JwtTokenProvider;
import com.threadly.auth.token.response.TokenResponse;
import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.auth.verification.PasswordVerificationUseCase;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.controller.auth.request.PasswordVerificationRequest;
import com.threadly.controller.auth.request.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  private final EmailVerificationUseCase emailVerificationUseCase;
  private final PasswordVerificationUseCase passwordVerificationUseCase;

  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 로그인
   *
   * @param userLoginRequest
   * @return
   */
  @PostMapping("/login")
  public TokenResponse login(@RequestBody UserLoginRequest userLoginRequest) {
    return authService.login(userLoginRequest);
  }

  /**
   * email 인증 'https://threadly.com/api/auth/verify?code={code}'
   *
   * @param code
   */
  @GetMapping("/verify-email")
  public void verifyMail(@RequestParam String code) {
    emailVerificationUseCase.verificationEmail(code);
  }

  /**
   * 사용자 정보 수정을 위한 비밀번호 재인증
   *
   * @param request
   * @return
   */
  @PostMapping("/verify-password")
  public PasswordVerificationToken verifyPassword(
      @RequestBody PasswordVerificationRequest request) {

    /*userId 추출*/
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userId = (String) auth.getCredentials();

    return
        passwordVerificationUseCase.getPasswordVerificationToken(userId, request.getPassword());

  }


}
