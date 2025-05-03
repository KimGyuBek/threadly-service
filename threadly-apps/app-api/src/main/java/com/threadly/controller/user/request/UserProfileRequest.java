package com.threadly.controller.user.request;

import com.threadly.user.UserGenderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 프로필 초기 설정 요청 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class UserProfileRequest {

  private String nickname;
  private String statusMessage;
  private String bio;
  private UserGenderType gender;
  private String profileImageUrl;
}

