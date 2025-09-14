package com.threadly.core.port.user.out.profile.image;

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
