package com.threadly.core.port.post.out.image;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.port.post.out.image.projection.PostImageProjection;
import java.util.List;

/**
 * postImage 조회 관련 port
 */
public interface PostImageQueryPort {

  /**
   * postId, status로 이미지 목록 조회
   *
   * @param postId
   * @return
   */
  List<PostImageProjection> findAllByPostIdAndStatus(String postId, ImageStatus status);

  /**
   * 주어진 postId 리스트에 해당하는 게시글 이미지 목록 조회
   *
   * @param postId
   * @return
   */
  List<PostImageProjection> findVisibleByPostIds(List<String> postIds);


}
