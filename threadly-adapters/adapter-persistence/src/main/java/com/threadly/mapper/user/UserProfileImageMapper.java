package com.threadly.mapper.user;

import com.threadly.entity.user.UserProfileImageEntity;
import com.threadly.user.profile.image.UserProfileImage;

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
