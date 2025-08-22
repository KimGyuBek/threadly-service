package com.threadly.core.usecase.user.profile.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 프로필 정보 업데이트 command
 */
@Getter
@AllArgsConstructor
public class UpdateMyProfileCommand {

  private String userId;
  private String nickname;
  private String statusMessage;
  private String bio;
  private String phone;
  private String profileImageId;


}
