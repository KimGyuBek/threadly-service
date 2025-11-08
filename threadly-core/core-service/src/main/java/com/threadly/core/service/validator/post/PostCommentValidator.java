package com.threadly.core.service.validator.post;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시글 댓글 Validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostCommentValidator {

  private final PostCommentQueryPort postCommentQueryPort;

  /**
   * 주어진 coommentId에 해당하는 게시글 댓글 조회
   * <p>존재하지 않는 경우 예외 발생</p>
   *
   * @param commentId
   * @return
   * @throws PostCommentException
   */
  public PostComment getPostCommentOrThrow(String commentId) {
    return postCommentQueryPort.fetchById(commentId)
        .orElseThrow((() -> new PostCommentException(ErrorCode.POST_COMMENT_NOT_FOUND)));
  }

  public void validateAccessibleStatus(String commentId) {
    PostComment postComment = getPostCommentOrThrow(commentId);

    if (!postComment.getStatus().equals(PostCommentStatus.ACTIVE)) {
      log.warn("접근 불가능한 게시글 댓글: commentId={}, status={}", commentId, postComment.getStatus());
      throw new PostCommentException(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }
  }
}
