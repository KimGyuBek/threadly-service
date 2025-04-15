package com.threadly.controller.user;

import com.threadly.controller.user.request.UserRegisterRequest;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.response.UserRegistrationResponse;
import com.threadly.verification.EmailVerificationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public UserRegistrationResponse register(
      @Valid @RequestBody UserRegisterRequest request
  ) {

    /*회원 가입*/
    UserRegistrationResponse response = registerUserUseCase.register(
        UserRegistrationCommand.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build()
    );

    /*인증 메일 전송*/
    emailVerificationUseCase.sendVerificationEmail(response.getUserId());

    return response;
  }


}
