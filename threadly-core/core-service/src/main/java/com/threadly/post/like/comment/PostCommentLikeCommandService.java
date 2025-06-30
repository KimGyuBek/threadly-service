package com.threadly.post.like.comment;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.CannotLikePostCommentException;
import com.threadly.post.comment.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 관련 Service
 */
@Service
@RequiredArgsConstructor
public class PostCommentLikeCommandService implements LikePostCommentUseCase,
    UnlikePostCommentUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;

  private final FetchPostCommentLikePort fetchPostCommentLikePort;
  private final CreatePostCommentLikePort createPostCommentLikePort;
  private final DeletePostCommentLikePort deletePostCommentLikePort;

  @Transactional
  @Override
  public LikePostCommentApiResponse likePostComment(LikePostCommentCommand command) {

    /*게시글 댓글 조회*/
    PostComment postComment = getPostComment(command);

    /*좋아요 가능한지 검증*/
    validateLikeable(postComment);

    /*좋아요 누르지 않았다면*/
    if (!isUserLiked(command)) {
      /*좋아요 처리*/
      createPostCommentLikePort.createPostCommentLike(
          postComment.like(command.getUserId()));
    }

    /*좋아요 수 조회*/

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        getLikeCount(command)
    );
  }


  @Transactional
  @Override
  public LikePostCommentApiResponse cancelPostCommentLike(LikePostCommentCommand command) {
    /*게시글 댓글 조회*/
    PostComment postComment = getPostComment(command);

    /*좋아요 취소 가능한 상태인지 검증*/
    validateLikeable(postComment);

    /*좋아요를 누른 상태인 경우*/
    if (isUserLiked(command)) {
      /*좋아요 삭제*/
      deletePostCommentLikePort.deletePostCommentLike(command.getCommentId(), command.getUserId());
    }

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        getLikeCount(command)
    );
  }

  /**
   * 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long getLikeCount(LikePostCommentCommand command) {
    long likeCount = fetchPostCommentLikePort.fetchLikeCountByCommentId(
        command.getCommentId());
    return likeCount;
  }

  /**
   * 게시글 댓글 조회
   *
   * @param command
   * @return
   */
  private PostComment getPostComment(LikePostCommentCommand command) {
    return
        fetchPostCommentPort.fetchById(command.getCommentId())
            .orElseThrow(() -> new PostCommentException(
                ErrorCode.POST_COMMENT_NOT_FOUND));
  }

  /**
   * 좋아요 가능한지 검증
   *
   * @param postComment
   */
  private static void validateLikeable(PostComment postComment) {
    try {
      postComment.validateLikeable();
    } catch (CannotLikePostCommentException e) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED);
    }
  }

  /**
   * 사용자가 이미 좋아요 눌렀는지 검증
   *
   * @param command
   * @return
   */
  private boolean isUserLiked(LikePostCommentCommand command) {
    return fetchPostCommentLikePort.existsByCommentIdAndUserId(command.getCommentId(),
        command.getUserId());
  }
}
