package com.threadly.core.port.image;

import com.threadly.commons.file.UploadImage;
import java.util.List;

/**
 * 게시글 이미지 저장 port
 */
public interface UploadImagePort {

  /**
   *게시글 이미지 파일 리스트 업로드
   *
   * @return
   */
  List<UploadImageResponse> uploadPostImageList(List<UploadImage> uploadImages);

  /**
   * 프로필 이미지 업로드
   * @param uploadImage
   * @return
   */
  UploadImageResponse uploadProfileImage(UploadImage uploadImage);

}
