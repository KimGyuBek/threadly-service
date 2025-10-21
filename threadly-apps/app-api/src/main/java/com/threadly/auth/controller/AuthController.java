package com.threadly.auth.controller;

import com.threadly.auth.AuthManager;
import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;
import com.threadly.core.port.auth.in.token.response.TokenReissueApiResponse;
import com.threadly.core.port.auth.in.verification.EmailVerificationUseCase;
import com.threadly.core.port.auth.in.verification.PasswordVerificationUseCase;
import com.threadly.core.port.auth.in.verification.response.PasswordVerificationToken;
import com.threadly.auth.request.PasswordVerificationRequest;
import com.threadly.auth.request.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


  private final EmailVerificationUseCase emailVerificationUseCase;
  private final PasswordVerificationUseCase passwordVerificationUseCase;
  private final AuthManager authManager;


  /**
   * 로그인
   *
   * @param userLoginRequest
   * @return
   */
//  @PreAuthorize("hasRole('USER')")
  @PostMapping("/login")
  public LoginTokenApiResponse login(@RequestBody UserLoginRequest userLoginRequest) {
    return authManager.login(userLoginRequest.getEmail(), userLoginRequest.getPassword());
  }

  /**
   * 로그아웃
   * @param accessToken
   */
  @PostMapping("/logout")
  public void logout(@RequestHeader(value = "Authorization", required = false) String accessToken) {
    authManager.logout(accessToken);
  }

  /**
   * refreshToken으로 login Token 재발급
   *
   * @param refreshToken
   * @return
   */
  @PostMapping("/reissue")
  public TokenReissueApiResponse reissueAccessToken(
      @RequestHeader(value = "X-refresh-token", required = false) String refreshToken) {
    return
        authManager.reissueLoginToken(refreshToken);
  }

  /**
   * 이메일 인증
   * @param code
   */
  @GetMapping("/verify-email")
  public void verifyMail(@RequestParam String code) {
    emailVerificationUseCase.verifyEmail(code);
  }

  /**
   * 2FA 인증
   *
   * @param request
   * @return
   */
  @PostMapping("/verify-password")
  public PasswordVerificationToken verifyPassword(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestBody PasswordVerificationRequest request) {

    return
        passwordVerificationUseCase.getPasswordVerificationToken(user.getUserId(), request.getPassword());

  }


}
