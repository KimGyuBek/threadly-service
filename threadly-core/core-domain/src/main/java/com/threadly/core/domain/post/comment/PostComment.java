package com.threadly.core.domain.post.comment;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.BlockedException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.ParentPostInactiveException;
import com.threadly.core.domain.post.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.commons.utils.RandomUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 게시글 댓글
 */
@Getter
public class PostComment {

  private String commentId;
  private String postId;
  private String userId;
  private String content;
  private PostCommentStatus status;
  private LocalDateTime createdAt;

  private List<CommentLike> commentLikes = new ArrayList<>();

  /**
   * 새로운 댓글 생성
   *
   * @param postId
   * @param userId
   * @param content
   * @return
   */
  public static PostComment newComment(String postId, String userId, String content) {
    return new PostComment(
        RandomUtils.generateNanoId(),
        postId,
        userId,
        (content != null) ? content : "",
        PostCommentStatus.ACTIVE,
        LocalDateTime.now()
    );
  }

  /**
   * 게시글 댓글 상태 DELETED로 변경
   */
  public void markAsDeleted() {
    /*상태 검증*/
    if (status == PostCommentStatus.DELETED) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_ALREADY_DELETED);
    } else if (status == PostCommentStatus.BLOCKED) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_BLOCKED);
    }

    this.status = PostCommentStatus.DELETED;
  }

  /**
   * 게시글 댓글이 삭제 가능 상태인지 검증
   *
   * @param userId
   * @param postStatus
   */
  public void validateDeletableBy(String userId, PostStatus postStatus) {
    if (!this.userId.equals(userId)) {
      throw new WriteMismatchException();
    } else if (this.status == PostCommentStatus.DELETED) {
      throw new AlreadyDeletedException();
    } else if (this.status == PostCommentStatus.BLOCKED) {
      throw new BlockedException();
    } else if (postStatus != PostStatus.ACTIVE) {
      throw new ParentPostInactiveException();
    }
  }

  /**
   * 댓글 좋아요 수 조회
   *
   * @return
   */
  public int getLikesCount() {
    return commentLikes == null ? 0 : commentLikes.size();
  }

  /**
   * 댓글 좋아요 목록 조회
   *
   * @return
   */
  public List<CommentLike> getLikesList() {
    return new ArrayList<>(commentLikes);
  }


  /**
   * 댓글 좋아요 가능한지 검증
   */
  public void validateLikeable() {
    if (this.status != PostCommentStatus.ACTIVE) {
      throw new CannotLikePostCommentException();
    }
  }

  /**
   * 댓글 좋아요
   *
   * @param userId
   */
  public CommentLike like(String userId) {
    return
        CommentLike.newLike(this.commentId, userId);
  }

  /**
   * 매핑 전용 생성자
   *
   * @param commentId
   * @param postId
   * @param userId
   * @param content
   * @param status
   */
  public PostComment(String commentId, String postId, String userId, String content,
      PostCommentStatus status, LocalDateTime createdAt) {
    this.commentId = commentId;
    this.postId = postId;
    this.userId = userId;
    this.content = content;
    this.status = status;
    this.createdAt = createdAt;
  }

  /*Test*/
  @VisibleForTesting
  void setCommentId(String commentId) {
    this.commentId = commentId;
  }

  /**
   * 테스트용
   *
   * @param commentId
   * @param postId
   * @param userId
   * @param content
   * @param status
   * @return
   */
  @VisibleForTesting
  public static PostComment newTestComment(String commentId, String postId, String userId,
      String content,
      PostCommentStatus status) {
    return new PostComment(
        commentId,
        postId,
        userId,
        content,
        status,
        LocalDateTime.now()

    );
  }
}

