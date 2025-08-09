package com.threadly.user.request.me;

import com.threadly.annotation.PasswordEncryption;
import com.threadly.user.account.command.dto.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 Request 객체
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChangePasswordRequest {

  @NotNull
  @NotBlank
  @PasswordEncryption
  private String newPassword;

  /**
   * request -> command
   *
   * @param userId
   * @return
   */
  public ChangePasswordCommand toCommand(String userId) {
    return new ChangePasswordCommand(
        userId, this.newPassword);
  }

}
