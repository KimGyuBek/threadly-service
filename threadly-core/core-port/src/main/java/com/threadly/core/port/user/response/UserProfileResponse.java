package com.threadly.core.port.user.response;

import com.threadly.core.domain.user.UserGenderType;
import com.threadly.core.domain.user.profile.UserProfileType;
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
