package com.threadly.adapter.persistence.core.user.request.me;

import com.threadly.core.usecase.user.profile.command.dto.UpdateMyPrivacySettingCommand;

/**
 * 내 계정 비공개 처리 요청 객체
 */
public record UpdateMyPrivacySettingRequest(boolean isPrivate) {


  /**
   * request -> command
   * @param userId
   * @return
   */
  public UpdateMyPrivacySettingCommand toCommand(String userId) {
    return new UpdateMyPrivacySettingCommand(
        userId, this.isPrivate
    );
  }

}
