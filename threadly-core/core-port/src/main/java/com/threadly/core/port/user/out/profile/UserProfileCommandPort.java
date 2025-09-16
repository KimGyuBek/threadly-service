package com.threadly.core.port.user.out.profile;

import com.threadly.core.domain.user.profile.UserProfile;

/**
 * UserProfile Command Port
 */
public interface UserProfileCommandPort {

  /**
   * UserProfile 저장
   * @param userProfile
   */
  void saveUserProfile(UserProfile userProfile);

  /**
   * 주어진 userProfile domain으로 userProfile 업데이트
   *
   * @param userProfile
   */
  void updateMyProfile(UserProfile userProfile);

}
