package com.threadly.core.service.validator.post;

import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시글 댓글 좋아요 validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostCommentLikeValidator {

  private final PostCommentLikeQueryPort postCommentLikeQueryPort;

  /**
   * 주어진 userId에 해당하는 사용자가 commentid에 해당하는 게시글에 이미 좋아요 눌렀는지 검증
   *
   * @param commentId
   * @param userId
   * @return
   */
  public boolean isUserLiked(String commentId, String userId) {
    return postCommentLikeQueryPort.existsByCommentIdAndUserId(commentId, userId);
  }
}
