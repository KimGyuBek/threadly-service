package com.threadly.core.port.post.in.query;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementQuery;
import com.threadly.core.port.post.in.query.dto.GetPostsQuery;
import com.threadly.core.port.post.in.query.dto.GetPostQuery;
import com.threadly.core.port.post.in.query.dto.GetUserPostsQuery;
import com.threadly.core.port.post.in.query.dto.PostDetails;

/**
 * 게시글 keyword 관련 usecase
 */
public interface PostQueryUseCase {

  /**
   * postId로 게시글 조회
   *
   * @param postId
   * @return
   */
  PostDetails getPost(GetPostQuery query);

  /**
   * 사용자가 조회 가능한 게시글 목록 조회
   *
   * @param query
   * @return
   */
  CursorPageApiResponse getUserVisiblePosts(GetPostsQuery query);

  /**
   * 사용자 작성 게시글 목록 조회
   *
   * @param query
   * @return
   */
  CursorPageApiResponse getUserPosts(GetUserPostsQuery query);

  /**
   * 게시글 활동 조회
   *
   * @param query
   * @return
   */
  GetPostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query);


}
