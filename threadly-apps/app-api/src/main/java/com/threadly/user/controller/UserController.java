package com.threadly.user.controller;

import com.threadly.core.usecase.user.account.command.RegisterUserUseCase;
import com.threadly.core.usecase.user.account.command.dto.RegisterUserApiResponse;
import com.threadly.user.request.UserRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  /**
   * 회원 가입
   *
   * @param request
   * @return
   */
  @PostMapping("")
  public ResponseEntity<RegisterUserApiResponse> register(
      @Valid @RequestBody UserRegisterRequest request
  ) {
    /*회원 가입*/
    return ResponseEntity.ok().body(
        registerUserUseCase.register(request.toCommand())
    );
  }
}
