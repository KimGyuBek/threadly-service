package com.threadly.user.profile.update;

/**
 * 사용자 프로필 정보 업데이트 관련 UseCase
 */
public interface UpdateMyProfileUseCase {

  /**
   * 사용자 프로필 정보 업데이트
   * @return
   */
  void updateMyProfile(UpdateMyProfileCommand command);

}
