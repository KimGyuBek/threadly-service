package com.threadly.core.port.user.in.profile.command;

import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileCommand;
import com.threadly.core.port.user.in.profile.command.dto.UpdateMyProfileCommand;

public interface UserProfileCommandUseCase {


  /**
   * 내 프로필 등록
   *
   * @param command
   */
  void registerMyProfile(RegisterMyProfileCommand command);

  /**
   * 내 프로필 정보 업데이트
   *
   * @param command
   */
  void updateMyProfile(UpdateMyProfileCommand command);
}
