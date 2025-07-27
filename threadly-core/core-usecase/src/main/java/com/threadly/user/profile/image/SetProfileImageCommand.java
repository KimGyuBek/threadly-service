package com.threadly.user.profile.image;

import com.threadly.file.UploadImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 프로필 이미지 설정 command
 */
@Getter
@AllArgsConstructor
public class SetProfileImageCommand {

  private String userId;
  private UploadImage uploadImage;

}
