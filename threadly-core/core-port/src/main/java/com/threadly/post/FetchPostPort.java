package com.threadly.post;

import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.Post;
import java.util.List;
import java.util.Optional;

/**
 * 게시글 조회 관련 port
 */
public interface FetchPostPort {

  /**
   * postId로 post 조회
   *
   * @param postId
   * @return
   */
  Optional<Post> findById(String postId);

  /**
   * postId로 게시글 상세 조회
   *
   * @param postId
   * @return
   */
  Optional<PostDetailResponse> fetchPostDetailsByPostId(String postId);

  /**
   * 게시글 상세 리스트 조회
   *
   * @return
   */
  List<PostDetailResponse> fetchPostDetailsList();

}
