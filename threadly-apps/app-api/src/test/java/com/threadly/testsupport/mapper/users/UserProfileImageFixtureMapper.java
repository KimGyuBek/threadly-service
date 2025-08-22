package com.threadly.testsupport.mapper.users;

import com.threadly.testsupport.dto.users.UserProfileImageFixtureDto;
import com.threadly.core.domain.user.profile.image.UserProfileImage;

/**
 * User Profile Image Fixture 객체 매퍼
 */
public class UserProfileImageFixtureMapper {

  /**
   * FixtureDto -> rDomain
   *
   * @param fixture
   * @return
   */
  public static UserProfileImage toDomain(UserProfileImageFixtureDto fixture) {
    return UserProfileImage.builder()
        .userProfileImageId(fixture.getUserProfileImageId())
        .storedFileName(fixture.getStoredFileName())
        .imageUrl(fixture.getImageUrl())
        .userId(fixture.getUserId())
        .status(fixture.getStatus())
        .build();
  }

}
