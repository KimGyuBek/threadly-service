package com.threadly.core.port.post.out.image;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostImage;

/**
 * 게시글 이미지 command 관련 port
 */
public interface PostImageCommandPort {

  /**
   * 게시글 이미지 메타 데이터 저장
   *
   * @param postImage
   */
  void savePostImage(PostImage postImage);

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

  /**
   * 주어딘 imageId에 해당하는 이미지 메타 데이터의 postId 업데이트
   *
   * @param imageId
   * @param postId
   */
  void updatePostId(String imageId, String postId);

  /**
   * 업로드 이미지 확정 처리
   *
   * @param imageId
   * @param postId
   * @param order
   */
  void finalizeImage(String imageId, String postId, int order);

}
