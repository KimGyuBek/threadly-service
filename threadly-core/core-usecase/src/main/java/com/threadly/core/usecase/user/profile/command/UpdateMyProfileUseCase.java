package com.threadly.core.usecase.user.profile.command;

import com.threadly.core.usecase.user.profile.command.dto.UpdateMyProfileCommand;

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
