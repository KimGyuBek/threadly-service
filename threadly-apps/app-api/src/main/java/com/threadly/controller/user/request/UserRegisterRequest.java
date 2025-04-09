package com.threadly.controller.user.request;

import com.threadly.PasswordEncryption;
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

}
