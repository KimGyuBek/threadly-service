package com.threadly.user.account;


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
