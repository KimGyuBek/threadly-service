package com.threadly.core.usecase.user.account.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 비밀번호 변경 usecase
 */
@Getter
@AllArgsConstructor
public class ChangePasswordCommand {

  private String userId;
  private String newPassword;

}
