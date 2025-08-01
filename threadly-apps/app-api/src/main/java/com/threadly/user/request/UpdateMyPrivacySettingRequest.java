package com.threadly.user.request;

import com.threadly.user.profile.update.UpdateMyPrivacySettingCommand;

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
