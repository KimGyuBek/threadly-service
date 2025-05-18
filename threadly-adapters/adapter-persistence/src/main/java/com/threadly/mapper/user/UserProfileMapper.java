package com.threadly.mapper.user;

import com.threadly.entity.user.UserProfileEntity;
import com.threadly.user.UserProfile;

public class UserProfileMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static UserProfile toDomain(UserProfileEntity entity) {
    return UserProfile.builder()
        .userProfileId(entity.getUserProfileId())
        .nickname(entity.getNickname())
        .statusMessage(entity.getStatusMessage())
        .bio(entity.getBio())
        .gender(entity.getGender())
        .profileType(entity.getProfileType())
        .profileImageUrl(entity.getProfileImageUrl())
        .build();
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static UserProfileEntity toEntity(UserProfile domain) {
    UserProfileEntity userProfileEntity;

    /*새로 생성하는 경우*/
    if (domain.getUserProfileId() == null) {
      userProfileEntity = UserProfileEntity.newUserProfile(
          domain.getNickname(),
          domain.getStatusMessage(),
          domain.getBio(),
          domain.getGender(),
          domain.getProfileType(),
          domain.getProfileImageUrl());

      /*이미 존재하는 경우*/
    } else {
      userProfileEntity = new UserProfileEntity(
          domain.getUserProfileId(),
          domain.getNickname(),
          domain.getStatusMessage(),
          domain.getBio(),
          domain.getGender(),
          domain.getProfileType(),
          domain.getProfileImageUrl());
    }
    return userProfileEntity;
  }

}
