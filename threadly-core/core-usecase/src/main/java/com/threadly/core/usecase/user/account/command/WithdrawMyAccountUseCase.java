package com.threadly.core.usecase.user.account.command;

/**
 * 사용자 탈퇴 관련 usecase
 */
public interface WithdrawMyAccountUseCase {

  /**
   * 내 계정 탈퇴처리
   *
   * @param userId
   */
  void withdrawMyAccount(String userId, String bearerToken);

}
