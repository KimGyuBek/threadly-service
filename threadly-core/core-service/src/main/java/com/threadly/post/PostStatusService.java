package com.threadly.post;

import com.threadly.post.response.PostStatusApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 게시글 좋아요/댓글 수 등 통계 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostStatusService implements PostStatusUseCase {


  @Override
  public PostStatusApiResponse getStatus(String postId) {
    return null;
  }
}
