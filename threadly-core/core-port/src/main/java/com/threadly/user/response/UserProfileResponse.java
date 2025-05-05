package com.threadly.user.response;

import com.threadly.user.UserGenderType;
import com.threadly.user.UserProfileType;
import lombok.Builder;
import lombok.Getter;

/**
 * UserProfileResponse DTO
 */
@Getter
@Builder
public class UserProfileResponse {

  private String userName;

  private String nickname;

  private String statusMessage;

  private String bio;

  private UserGenderType gender;

  private String profileImageUrl;

  private UserProfileType userProfileType;

}
