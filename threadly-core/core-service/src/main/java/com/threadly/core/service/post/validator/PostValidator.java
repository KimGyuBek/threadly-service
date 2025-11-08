package com.threadly.core.service.post.validator;

import static com.threadly.core.domain.post.PostStatus.BLOCKED;
import static com.threadly.core.domain.post.PostStatus.DELETED;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시글 Validator
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {

  private final PostQueryPort postQueryPort;

  /**
   * 주어진 postId에 해당하는 게시글 조회
   * <p>
   * 존재하지 않는 경우 예외 발생
   * </p>
   *
   * @param postId
   * @return
   */
  public Post getPostOrThrow(String postId) {
    return postQueryPort.fetchById(postId).orElseThrow(
        () -> new PostException(ErrorCode.POST_NOT_FOUND)
    );
  }

  /**
   * 주어진 postId에 해당하는 게시글 상태 조회
   * <p>존재하지 않는 경우 예외 발생</p>
   *
   * @param postId
   * @return
   * @throws PostException
   */
  public PostStatus getPostStatusOrThrow(String postId) {
    return postQueryPort.fetchPostStatusByPostId(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }

  /**
   * 사용자가 게시글을 업데이트할 수 있는지 검증
   *
   * @param authorId
   * @param requesterId
   */
  public void validateUpdatableBy(String authorId, String requesterId) {
    if (!authorId.equals(requesterId)) {
      log.info("작성자와 요청자가 일치하지 않음: authorId={}, requesterId={}", authorId, requesterId);
      throw new PostException(ErrorCode.POST_UPDATE_FORBIDDEN);
    }
  }

  /**
   * 주어진 postId, userId에 해당하는 게시글의 PostDetailsProjection 조회
   *
   * @param postId
   * @param userId
   * @return
   * @throws PostException
   */
  public PostDetailProjection getPostDetailsProjectionOrElseThrow(String postId, String userId) {
    return postQueryPort.fetchPostDetailsByPostIdAndUserId(postId, userId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }

  /**
   * 게시글 상태 검증
   *
   * @param status
   */
  public void validatePostStatus(String postId, PostStatus status) {
    if (status == DELETED) {
      log.warn("이미 삭제처리된 게시글: postId={}", postId);
      throw new PostException(ErrorCode.POST_ALREADY_DELETED_ACTION);
    }

    if (status == BLOCKED) {
      log.warn("이미 BLOCKED된 게시글: postId={}", postId);
      throw new PostException(ErrorCode.POST_DELETE_BLOCKED);
    }
  }

  /**
   * 주어진 status가 접근 가능한 상태인지 검증
   *
   * @param status
   */
  public void validateAccessibleStatus(PostStatus status) {
    log.debug("postStatus={}", status);

    switch (status) {
      case DELETED -> throw new PostException(ErrorCode.POST_ALREADY_DELETED);
      case ARCHIVE -> throw new PostException(ErrorCode.POST_NOT_FOUND);
      case BLOCKED -> throw new PostException(ErrorCode.POST_BLOCKED);
      default -> {
        log.debug("접근 가능한 게시글");
      }
    }
  }

  /**
   * 주어진 postId에 해당하는 게시글이 접근 가능한 상태인지 검증
   *
   * @param postId
   * @return ststus
   */
  public PostStatus validateAccessibleStatusById(String postId) {
    PostStatus status = postQueryPort.fetchPostStatusByPostId(postId).orElseThrow(
        () -> {
          log.warn("게시글이 존재하지 않음, postId={}", postId);
          return new PostException(ErrorCode.POST_NOT_FOUND);
        }
    );
    validateAccessibleStatus(status);

    return status;
  }


}
