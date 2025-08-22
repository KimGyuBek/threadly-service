package com.threadly.core.usecase.user.account.command;

/**
 * 내 계정 비활성화 관련 UseCase
 */
public interface DeactivateMyAccountUseCase {

  /**
   * 주어진 파라미터로 내 계정 비활성화
   * @param userId
   * @param bearerToken
   */
  void deactivateMyAccount(String userId, String bearerToken);

}
