package com.threadly.user.profile.save;

import com.threadly.user.profile.UserProfile;

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
