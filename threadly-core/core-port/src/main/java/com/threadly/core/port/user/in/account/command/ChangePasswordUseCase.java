package com.threadly.core.port.user.in.account.command;


import com.threadly.core.port.user.in.account.command.dto.ChangePasswordCommand;

/**
 * 비밀번호 변경 관련 usecase
 */
public interface ChangePasswordUseCase {

  /**
   * 비밀번호 변경
   *
   * @param command
   */
  void changePassword(ChangePasswordCommand command);

}
