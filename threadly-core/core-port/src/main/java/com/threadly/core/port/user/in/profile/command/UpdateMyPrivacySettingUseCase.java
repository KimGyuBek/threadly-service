package com.threadly.core.port.user.in.profile.command;

import com.threadly.core.port.user.in.profile.command.dto.UpdateMyPrivacySettingCommand;

/**
 * 내 계정 공개 여부 관련 usecae
 */
public interface UpdateMyPrivacySettingUseCase {

  /**
   * 내 프로필 공개 여부 관련 요청
   *
   * @param command
   */
  void updatePrivacy(UpdateMyPrivacySettingCommand command);

}
