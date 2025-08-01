package com.threadly.user.profile.update;

import com.threadly.user.profile.UserProfile;

/**
 * 내 프로필 업데이트 관련 port
 */
public interface UpdateMyProfilePort {

  /**
   * 주어진 userProfile domain으로 userProfile 업데이트
   * @param userProfile
   */
  void updateMyProfile(UserProfile userProfile);

}
