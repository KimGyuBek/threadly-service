package com.threadly.core.service.post.like.post;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.domain.post.CannotLikePostException;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostLike;
import com.threadly.core.port.post.fetch.FetchPostPort;
import com.threadly.core.port.post.like.post.CreatePostLikePort;
import com.threadly.core.port.post.like.post.DeletePostLikePort;
import com.threadly.core.port.post.like.post.FetchPostLikePort;
import com.threadly.core.service.notification.NotificationService;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import com.threadly.core.usecase.post.like.post.LikePostApiResponse;
import com.threadly.core.usecase.post.like.post.LikePostCommand;
import com.threadly.core.usecase.post.like.post.LikePostUseCase;
import com.threadly.core.usecase.post.like.post.UnlikePostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 생성 및 취소 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostLikeCommandService implements LikePostUseCase, UnlikePostUseCase {

  private final FetchPostPort fetchPostPort;

  private final FetchPostLikePort fetchPostLikePort;
  private final CreatePostLikePort createPostLikePort;
  private final DeletePostLikePort deletePostLikePort;

  private final ApplicationEventPublisher applicationEventPublisher;


  @Transactional
  @Override
  public LikePostApiResponse likePost(LikePostCommand command) {
    /*게시글 조회*/
    Post post = getPost(command);

    /*게시글이 좋아요 가능 상태인지 조회*/
    validateLikable(post);

    /*사용자가 좋아요 누르지 않았다면*/
    if (!isUserLiked(command)) {
      PostLike newLike = post.like(command.getUserId());

      /*좋아요 저장*/
      createPostLikePort.createPostLike(newLike);

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
    Post post = getPost(command);

    /*좋아요 취소 가능한 상태인지 검증*/
    validateLikable(post);

    /*사용자가 좋아요를 눌렀으면*/
    if (isUserLiked(command)) {

      /*좋아요 삭제 */
      deletePostLikePort.deleteByPostIdAndUserId(command.getPostId(), command.getUserId());
    }

    return new LikePostApiResponse(
        post.getPostId(),
        getLikeCount(command)
    );
  }

  /**
   * 게시글 조회
   *
   * @param command
   * @return
   */
  private Post getPost(LikePostCommand command) {
    Post post = fetchPostPort.fetchById(command.getPostId()).orElseThrow(() -> new PostException(
        ErrorCode.POST_NOT_FOUND));
    return post;
  }

  /**
   * postId로 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long getLikeCount(LikePostCommand command) {
    return fetchPostLikePort.fetchLikeCountByPostId(command.getPostId());
  }

  /**
   * 사용자가 해당 게시글에 좋아요를 눌렀는지 조회
   *
   * @param command
   * @return
   */
  private boolean isUserLiked(LikePostCommand command) {
    return fetchPostLikePort.existsByPostIdAndUserId(command.getPostId(), command.getUserId());
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
