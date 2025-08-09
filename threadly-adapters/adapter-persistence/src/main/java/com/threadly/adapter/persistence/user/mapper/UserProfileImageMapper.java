package com.threadly.adapter.persistence.user.mapper;

import com.threadly.adapter.persistence.user.entity.UserProfileImageEntity;
import com.threadly.core.domain.user.profile.image.UserProfileImage;

/**U
 * UserProfileImageMapper
 */
public class UserProfileImageMapper {

  //  /**
//   * entity -> domain
//   *
//   * @param entity
//   * @return
//   */
  public static UserProfileImage toDomain(UserProfileImageEntity entity) {
    return  UserProfileImage.builder()
        .userProfileImageId(entity.getUserProfileImageId())
        .build();
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static UserProfileImageEntity toEntity(UserProfileImage domain) {
    return UserProfileImageEntity.newUserProfileImage(
        domain.getUserProfileImageId(),
        null,
        domain.getStoredFileName(),
        domain.getImageUrl(),
        domain.getStatus()
    );
  }
}
