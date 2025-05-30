package com.threadly.testsupport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.user.UserGenderType;
import com.threadly.user.UserProfileType;
import com.threadly.user.UserType;
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

  @JsonProperty("isActive")
  private boolean isActive;
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

    private String userProfileId;
    private String nickname;
    private String statusMessage;
    private String bio;
    private UserGenderType gender;
    private UserProfileType profileType;
    private String profileImageUrl;
  }


}
