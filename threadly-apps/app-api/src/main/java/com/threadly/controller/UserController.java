package com.threadly.controller;

import com.threadly.controller.request.UserRegisterRequest;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegisterationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;

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

}
