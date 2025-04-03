package com.threadly.controller;

import com.threadly.controller.request.UserLoginRequest;
import com.threadly.controller.request.UserRegisterRequest;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserLoginResponse;
import com.threadly.user.response.UserRegisterationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final AuthenticationManagerBuilder authenticationMangerBuilder;


  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("/register")
  public UserRegisterationResponse register(
      @RequestBody UserRegisterRequest request
  ) {

    UserRegisterationResponse register = registerUserUseCase.register(
        UserRegisterationCommand.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build()
    );

    return register;
  }

  /**
   * 로그인
   */
  @PostMapping("/login")
  public UserLoginResponse login(@RequestBody UserLoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        email, password);

    Authentication authenticate = authenticationMangerBuilder.getObject()
        .authenticate(authentication);

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(authenticate);


    return UserLoginResponse.builder()
        .build();

  }


}
