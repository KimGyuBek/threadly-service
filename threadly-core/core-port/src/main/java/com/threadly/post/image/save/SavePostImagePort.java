package com.threadly.post.image.save;

import com.threadly.post.PostImage;

/**
 * 게시글 이미지 메타 데이터 저장 port
 */
public interface SavePostImagePort {

  /**
   * 게시글 이미지 메타 데이터 저장
   *
   * @param postImage
   */
  void savePostImage(PostImage postImage);

}
