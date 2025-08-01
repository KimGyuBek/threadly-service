package com.threadly.user.profile.get;

/**
 * 내 프로필 조회 관련 UseCase
 */
public interface GetMyProfileUseCase {

  /**
   * 내 프로필 상세 조회
   * @param userId
   * @return
   */
  GetMyProfileDetailsApiResponse getMyProfileDetails(String userId);
}
