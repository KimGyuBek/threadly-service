package com.threadly.core.service.post.validator;

import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시글 좋아요 validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostLikeValidator {

  private final PostLikeQueryPort postLikeQueryPort;

  /**
   * 사용자가 해당 게시글에 좋아요를 눌렀는지 검증
   *
   * @param postId
   * @param userId
   * @return
   */
  public boolean isUserLiked(String postId, String userId) {
    return postLikeQueryPort.existsByPostIdAndUserId(postId, userId);
  }
}
