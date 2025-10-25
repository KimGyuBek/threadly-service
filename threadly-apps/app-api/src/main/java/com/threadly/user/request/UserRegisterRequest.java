package com.threadly.user.request;

import com.threadly.annotation.PasswordEncryption;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegisterRequest {

  @NotNull
  @NotBlank
  private String email;

  @NotNull
  @NotBlank
  private String userName;

  @NotNull
  @NotBlank
  @PasswordEncryption
  private String password;

  @NotNull
  private String phone;

  public RegisterUserCommand toCommand() {
    return new RegisterUserCommand(email, userName, password, phone);
  }

}
