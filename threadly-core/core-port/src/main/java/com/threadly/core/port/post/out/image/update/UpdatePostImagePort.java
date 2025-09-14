package com.threadly.core.port.post.out.image.update;

import com.threadly.core.domain.image.ImageStatus;

/**
 * 게시글 이미지 업데이트 관련 port
 */
public interface UpdatePostImagePort {


  /**
   * 게시글 이미지 상태 변경
   *
   * @param imageId
   */
  void updateStatus(String imageId, ImageStatus status);

  /**
   * imageOrder update
   *
   * @param imageId
   * @param order
   */
  void updateImageOrder(String imageId, int order);

  void updatePostId(String imageId, String postId);

  /**
   * 업로드 이미지 확정 처리
   * @param imageId
   * @param postId
   * @param order
   */
  void finalizeImage(String imageId, String postId, int order);

}
