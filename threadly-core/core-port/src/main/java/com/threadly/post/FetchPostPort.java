package com.threadly.post;

import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.Post;
import com.threadly.posts.PostStatusType;
import java.time.LocalDateTime;
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
   * @param userId
   * @return
   *
   */
  Optional<PostDetailResponse> findPostDetailsByPostIdAndUserId(String postId, String userId);

  /**
   * 사용자가 조회 가능한 게시글 목록 조회
   *
   * @param userId
   * @return
   */
  List<PostDetailResponse> findUserVisiblePostList(String userId);

  /**
   * 사용자 조회 가능한 커서기반 게시글 목록 조회
   * @param userId
   * @param cursorPostedAt
   * @param limit
   * @return
   */
  List<PostDetailResponse> findUserVisiblePostListByCursor(String userId, LocalDateTime cursorPostedAt, String cursorPostId, int limit);


  /**
   * postId로 게시글 status 조회
   * @param postId
   * @return
   */
  Optional<PostStatusType> findPostStatusByPostId(String postId);

}
