package com.threadly.user.request;

import com.threadly.user.UserGenderType;
import com.threadly.user.profile.register.RegisterUserProfileCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 프로필 초기 설정 요청 DTO
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterUserProfileRequest {

  private String nickname;
  private String statusMessage;
  private String bio;
  private String phone;
  private UserGenderType gender;
  private String profileImageUrl;

  /**
   * request -> command
   * @param userId
   * @return
   */
  public RegisterUserProfileCommand toCommand(String userId) {
    return new RegisterUserProfileCommand(
        userId,
        nickname,
        statusMessage,
        bio,
        phone,
        gender,
        profileImageUrl
    );
  }
}

