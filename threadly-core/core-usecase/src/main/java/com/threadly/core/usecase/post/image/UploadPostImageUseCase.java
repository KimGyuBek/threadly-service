package com.threadly.core.usecase.post.image;

/**
 * 게시글 이미지 업로드 usecase
 */
public interface UploadPostImageUseCase {

  /**
   * 게시글 이미지 업로드
   *
   * @param command
   * @return
   */
  UploadPostImagesApiResponse uploadPostImages(UploadPostImageCommand command);


}
