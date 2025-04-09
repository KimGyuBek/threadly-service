package com.threadly.controller.user;

import com.threadly.auth.AuthService;
import com.threadly.controller.user.request.RequestMail;
import com.threadly.controller.user.request.UserRegisterRequest;
import com.threadly.mail.SendMailUseCase;
import com.threadly.user.RegisterUserUseCase;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.response.UserRegistrationResponse;
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
  private final AuthService authService;

  /*test*/
  private final SendMailUseCase sendMailUseCase;

  @PostMapping("/mailtest")
  public boolean testMail(@RequestBody RequestMail body) {
    sendMailUseCase.sendMail(body.getFrom(), body.getTo(), body.getSubject(), body.getBody());

    return true;

  }


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

    UserRegistrationResponse response = registerUserUseCase.register(
        UserRegistrationCommand.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(request.getPassword())
            .phone(request.getPhone())
            .build()
    );

    return response;
  }


}
