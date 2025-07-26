package com.threadly.user.withdraw;

/**
 * 사용자 탈퇴 관련 usecase
 */
public interface WithdrawUserUseCase {

  /**
   * 사용자 탈퇴처리
   *
   * @param userId
   */
  void withdrawUser(String userId, String bearerToken);

}
