package com.threadly.user.profile.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 프로필 정보 업데이트 command
 */
@Getter
@AllArgsConstructor
public class UpdateUserProfileCommand {

  private String userId;
  private String nickname;
  private String statusMessage;
  private String bio;
  private String phone;
  private String profileImageId;


}
