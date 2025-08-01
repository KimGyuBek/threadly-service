package com.threadly.user.profile.image;

/**
 * 내 프로필 이미지 업로드 usecase
 */
public interface SetMyProfileImageUseCase {

  /**
   * 내 프로필 이미지 설정
   *
   * @param command
   * @return
   */
  UploadMyProfileImageApiResponse setMyProfileImage(SetMyProfileImageCommand command);


}
