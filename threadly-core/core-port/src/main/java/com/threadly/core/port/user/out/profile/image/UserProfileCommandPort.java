package com.threadly.core.port.user.out.profile.image;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.user.profile.image.UserProfileImage;

/**
 * 사용자 프로필 이미지 생성 port
 */
public interface UserProfileCommandPort {

  /**
   * 이미지 생성
   *
   * @param domain
   */
  void create(UserProfileImage domain);

  /**
   * 주어진 profileImageId에 해당하는 사용자 프로필 이미지의 ststus를 변경한다.
   *
   * @param profileImageId
   * @param status
   */
  void updateStatusById(String profileImageId, ImageStatus status);

  /**
   * 주어진 imageId에 해당하는 사용자 프로필 이미지의 status를 변경한다.
   *
   * @param imageId
   * @param status
   */
  void updateStatusAndUserIdByImageId(String imageId, String userId, ImageStatus status);
}
