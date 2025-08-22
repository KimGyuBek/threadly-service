package com.threadly.adapter.persistence.user.mapper;

import com.threadly.adapter.persistence.user.entity.UserProfileEntity;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.domain.user.profile.image.UserProfileImage;

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
        .userProfileImage(UserProfileImage.builder().build())
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
