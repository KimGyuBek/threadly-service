package com.threadly.post.image.fetch;

import com.threadly.post.PostImage;
import java.util.List;
import java.util.Optional;

/**
 * postImage 조회 관련 port
 */
public interface FetchPostImagePort {

  /**
   * postId에 해당하는 이미지 목록 조회
   * @param postId
   * @return
   */
  List<PostImageProjection> fetchPostImageByPostId(String postId);

}
