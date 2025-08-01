package com.threadly.user.profile.register;

/**
 * 사용자 프로필 등록 관련 UseCase
 */
public interface RegisterMyProfileUseCase {

  /**
   * 내 프로필 등록
   * @param command
   */
  void registerMyProfile(RegisterMyProfileCommand command);

}
