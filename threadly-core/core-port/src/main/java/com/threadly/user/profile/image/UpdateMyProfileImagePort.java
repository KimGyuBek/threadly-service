package com.threadly.user.profile.image;

import com.threadly.image.ImageStatus;

/**
 * 프로필 이미지 업데이트 관련 Port
 */
public interface UpdateMyProfileImagePort {

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
