package com.threadly.core.port.user.profile.image;

import com.threadly.core.domain.user.profile.image.UserProfileImage;

/**
 * 사용자 프로필 이미지 생성 port
 */
public interface CreateMyProfileImagePort {

  /**
   * 이미지 생성
   *
   * @param domain
   */
  void create(UserProfileImage domain);


}
