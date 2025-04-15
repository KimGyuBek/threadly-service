package com.threadly.controller.auth;

import com.threadly.auth.AuthService;
import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.token.response.TokenResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
   * @param  code
   */
  @GetMapping("/verify")
  public void verifyMail(@RequestParam String code) {

  }


}
