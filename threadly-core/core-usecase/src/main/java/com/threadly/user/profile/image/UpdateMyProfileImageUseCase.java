package com.threadly.user.profile.image;

/**
 * 내 프로필 이미지 업데이트 관련 usecase
 */
public interface UpdateMyProfileImageUseCase {

  /**
   * 주어진 userId, profileImageId로 프로필 이미지 업데이트 또는 삭제
   * @param userId
   * @param profileImageId
   */
  void updateProfileImage(String userId, String profileImageId);

}
