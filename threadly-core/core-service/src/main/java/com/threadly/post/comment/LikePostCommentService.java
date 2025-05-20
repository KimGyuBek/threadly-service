package com.threadly.post.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.post.comment.like.CreatePostCommentLikePort;
import com.threadly.post.comment.like.FetchPostCommentLikePort;
import com.threadly.post.comment.like.LikePostCommentUseCase;
import com.threadly.post.comment.like.command.LikePostCommentCommand;
import com.threadly.post.comment.like.response.LikePostCommentApiResponse;
import com.threadly.posts.comment.CannotLikePostCommentException;
import com.threadly.posts.comment.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 관련 Service
 */
@Service
@RequiredArgsConstructor
public class LikePostCommentService implements LikePostCommentUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;

  private final FetchPostCommentLikePort fetchPostCommentLikePort;
  private final CreatePostCommentLikePort createPostCommentLikePort;

  @Transactional
  @Override
  public LikePostCommentApiResponse likePostComment(LikePostCommentCommand command) {

    /*게시글 댓글 조회*/
    PostComment postComment = fetchPostComment(command);

    /*좋아요 가능한지 검증*/
    validateLikeable(postComment);

    /*좋아요 누르지 않았다면*/
    if (!fetchPostCommentLikePort.existsByCommentIdAndUserId(command.getCommentId(),
        command.getUserId())) {

      /*좋아요 처리*/
      createPostCommentLikePort.createPostCommentLike(
          postComment.like(command.getUserId()));
    }

    /*좋아요 수 조회*/
    long likeCount = fetchLikeCount(command);

    return new LikePostCommentApiResponse(
        postComment.getCommentId(),
        likeCount
    );
  }

  /**
   * 좋아요 수 조회
   *
   * @param command
   * @return
   */
  private long fetchLikeCount(LikePostCommentCommand command) {
    long likeCount = fetchPostCommentLikePort.getLikeCountByCommentId(
        command.getCommentId());
    return likeCount;
  }

  /**
   * 게시글 댓글 조회
   *
   * @param command
   * @return
   */
  private PostComment fetchPostComment(LikePostCommentCommand command) {
    return
        fetchPostCommentPort.findById(command.getCommentId())
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
}
