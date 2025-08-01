package com.threadly.user.account;

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
