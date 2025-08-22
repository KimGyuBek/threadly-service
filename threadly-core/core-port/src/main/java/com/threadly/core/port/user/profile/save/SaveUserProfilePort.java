package com.threadly.core.port.user.profile.save;

import com.threadly.core.domain.user.profile.UserProfile;

/**
 * userProfile 생성 관련
 */
public interface SaveUserProfilePort {

  /**
   * UserProfile 저장
   * @param userProfile
   */
  void saveUserProfile(UserProfile userProfile);

}
