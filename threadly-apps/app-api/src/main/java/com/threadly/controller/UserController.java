package com.threadly.controller;

import com.threadly.auth.AuthService;
import com.threadly.controller.request.UserLoginRequest;
import com.threadly.controller.request.UserRegisterRequest;
import com.threadly.token.response.TokenResponse;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final AuthService authService;


  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("/register")
  public UserRegistrationResponse register(
      @RequestBody UserRegisterRequest request
  ) {

    UserRegistrationResponse response = registerUserUseCase.register(
        UserRegisterationCommand.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build()
    );

    return response;
  }

  /**
   * 로그인
   */
  @PostMapping("/login")
  public TokenResponse login(@RequestBody UserLoginRequest request) {

    return authService.login(request);
  }


}
