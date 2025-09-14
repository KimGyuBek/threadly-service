package com.threadly.core.port.user.in.profile.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 내 프로필 공개 여부 요청 관련 command
 */
@AllArgsConstructor
@Getter
public class UpdateMyPrivacySettingCommand {

  private String userId;
  private boolean isPrivate;

}
