package com.threadly.user.profile.register;

/**
 * 사용자 프로필 등록 관련 UseCase
 */
public interface RegisterUserProfileUseCase {

  /**
   * 사용자 프로필 등록
   * @param command
   */
  void registerUserProfile(RegisterUserProfileCommand command);

}
