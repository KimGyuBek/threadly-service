package com.threadly.user.profile.get;

public interface GetUserProfileUseCase {

  /**
   * userId에 해당하는 profile 존재 여부 조회
   * @param userId
   * @return
   */
  boolean existsUserProfile(String userId);

}
