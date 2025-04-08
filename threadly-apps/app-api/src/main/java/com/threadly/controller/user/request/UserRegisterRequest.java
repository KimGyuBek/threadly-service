package com.threadly.controller.user.request;

import com.threadly.PasswordEncryption;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegisterRequest {

  @NotNull
  private String email;

  @NotNull
  private String userName;

  @NotNull
  @PasswordEncryption
  private String password;

  @NotNull
  private String phone;

}
