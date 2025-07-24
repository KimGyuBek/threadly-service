package com.threadly.user.profile.fetch;

public interface FetchUserProfilePort {

  void findByUserId(String userId);

  /**
   * userId에 해당하는 user comment preview 프로젝션 조회
   * @param userId
   * @return
   */
  UserPreviewProjection findUserPreviewByUserId(String userId);

  /**
   * userId에 해당하는 profile 존재 유무
   * @param userId
   * @return
   */
  boolean existsUserProfileByUserId(String userId);
}
