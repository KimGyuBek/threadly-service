package com.threadly.user.profile.image;

/**
 * 사용자 프로필 이미지 업로드 usecase
 */
public interface SetUserProfileImageUseCase {

  /**
   * 사용자 프로필 이미지 설정
   * @param command
   * @return
   */
  SetProfileImageApiResponse setProfileImage(SetProfileImageCommand command);


}
