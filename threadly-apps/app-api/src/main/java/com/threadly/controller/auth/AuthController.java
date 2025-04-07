package com.threadly.controller.auth;

import com.threadly.auth.AuthService;
import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.token.TokenService;
import com.threadly.token.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;


  /**
   * 로그인
   * @param userLoginRequest
   * @return
   */
  @PostMapping("/login")
  public TokenResponse login(@RequestBody UserLoginRequest userLoginRequest) {
    return authService.login(userLoginRequest);
  }



}
