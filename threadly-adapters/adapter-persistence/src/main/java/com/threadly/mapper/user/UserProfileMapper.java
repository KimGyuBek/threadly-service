package com.threadly.mapper.user;

import com.threadly.entity.user.UserProfileEntity;
import com.threadly.user.profile.UserProfile;

public class UserProfileMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static UserProfile toDomain(UserProfileEntity entity) {
    return UserProfile.builder()
        .userId(entity.getUserId())
        .nickname(entity.getNickname())
        .statusMessage(entity.getStatusMessage())
        .bio(entity.getBio())
        .genderType(entity.getGender())
        .userProfileType(entity.getProfileType())
//        .profileImageUrl(entity.getProfileImageUrl())
        .build();
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static UserProfileEntity toEntity(UserProfile domain) {
    return
        UserProfileEntity.newUserProfile(
            domain.getUserId(),
            domain.getNickname(),
            domain.getStatusMessage(),
            domain.getBio(),
            domain.getGenderType(),
            domain.getUserProfileType()
        );
  }
}
