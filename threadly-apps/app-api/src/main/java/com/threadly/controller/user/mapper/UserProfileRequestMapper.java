package com.threadly.controller.user.mapper;

import com.threadly.controller.user.request.UserProfileRequest;
import com.threadly.user.command.UserSetProfileCommand;
import org.springframework.stereotype.Component;

/**
 * UserProfileRequestMapper
 */
@Component
public class UserProfileRequestMapper {

  /**
   * request -> command
   * @param request
   * @return
   */
  public UserSetProfileCommand toCommand(UserProfileRequest request) {
    return UserSetProfileCommand.builder()
        .nickname(request.getNickname())
        .statusMessage(request.getStatusMessage())
        .bio(request.getBio())
        .gender(request.getGender())
        .profileImageUrl(request.getProfileImageUrl())
        .build();

  }

}
