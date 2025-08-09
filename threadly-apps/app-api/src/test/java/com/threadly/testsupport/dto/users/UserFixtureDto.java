package com.threadly.testsupport.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.domain.user.UserType;
import com.threadly.core.domain.user.profile.UserProfileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 Dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFixtureDto {

  private String userId;
  private String userName;
  private String password;
  private String email;
  private String phone;
  private UserType userType;
  private UserStatusType userStatusType;

  @JsonProperty("isEmailVerified")
  private boolean isEmailVerified;
  private UserProfileFixtureDto userProfile;

  /**
   * 사용자 프로필 Dto
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserProfileFixtureDto {

    private String nickname;
    private String statusMessage;
    private String bio;
    private UserGenderType gender;
    private UserProfileType profileType;
  }


}
