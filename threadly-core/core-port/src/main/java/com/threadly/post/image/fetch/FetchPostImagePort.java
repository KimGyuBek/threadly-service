package com.threadly.post.image.fetch;

import com.threadly.post.PostImageStatus;
import java.util.List;

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

  /**
   * postId, status로 이미지 목록 조회
   * @param postId
   * @return
   */
  List<PostImageProjection> findAllByPostIdAndStatus(String postId, PostImageStatus status);



}
