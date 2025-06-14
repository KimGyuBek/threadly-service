package com.threadly.post.image.upload;

import com.threadly.file.UploadImage;
import java.util.List;

/**
 * 게시글 이미지 저장 port
 */
public interface UploadPostImagePort {

  /**
   * 이미지 파일 업로드
   *
   * @return
   */
  List<UploadImageResponse> uploadPostImage(List<UploadImage> uploadImages);

}
