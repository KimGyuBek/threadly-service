package com.threadly.core.service.post.like.post;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.domain.post.CannotLikePostException;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostLike;
import com.threadly.core.port.post.in.like.post.command.PostLikeCommandUseCase;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostApiResponse;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostCommand;
import com.threadly.core.port.post.out.like.post.PostLikeCommandPort;
import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.service.post.validator.PostLikeValidator;
import com.threadly.core.service.post.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요  command Service
 */
@Service
@RequiredArgsConstructor
public class PostLikeCommandService implements PostLikeCommandUseCase {

  private final PostValidator postValidator;
  private final PostLikeValidator postLikeValidator;

  private final PostLikeQueryPort postLikeQueryPort;
  private final PostLikeCommandPort postLikeCommandPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public LikePostApiResponse likePost(LikePostCommand command) {
    /*게시글 조회*/
    Post post = postValidator.getPostOrThrow(command.getPostId());

    /*게시글이 좋아요 가능 상태인지 조회*/
    validateLikable(post);

    /*사용자가 좋아요 누르지 않았다면*/
    if (!postLikeValidator.isUserLiked(command.getPostId(), command.getUserId())) {
      PostLike newLike = post.addLike(command.getUserId());

      /*좋아요 저장*/
      postLikeCommandPort.createPostLike(newLike);

      /*알림 발행*/
      applicationEventPublisher.publishEvent(
          new NotificationPublishCommand(
              post.getUserId(),
              command.getUserId(),
              NotificationType.POST_LIKE,
              new PostLikeMeta(post.getPostId()))
      );
    }

    long likeCount = getLikeCount(command);

    return new LikePostApiResponse(
        post.getPostId(),
        likeCount
    );
  }


  @Transactional
  @Override
  public LikePostApiResponse cancelLikePost(LikePostCommand command) {
    /*게시글 조회*/
    Post post = postValidator.getPostOrThrow(command.getPostId());

    /*좋아요 취소 가능한 상태인지 검증*/
    validateLikable(post);

    /*사용자가 좋아요를 눌렀으면*/
    if (postLikeValidator.isUserLiked(command.getPostId(), command.getUserId())) {

      /*좋아요 삭제 */
      postLikeCommandPort.deleteByPostIdAndUserId(command.getPostId(), command.getUserId());
    }

    return new LikePostApiResponse(
        post.getPostId(),
        getLikeCount(command)
    );
  }

  /**
   * postId로 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long getLikeCount(LikePostCommand command) {
    return postLikeQueryPort.fetchLikeCountByPostId(command.getPostId());
  }

  /**
   * 게시글이 좋아요 가능한 상태인지 검증
   *
   * @param post
   */
  private static void validateLikable(Post post) {
    try {
      post.validateLikable();
    } catch (CannotLikePostException e) {
      throw new PostException(ErrorCode.POST_LIKE_NOT_ALLOWED);
    }
  }
}
