package com.threadly.posts.comment;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.posts.PostCommentStatusType;
import com.threadly.posts.PostStatusType;
import com.threadly.posts.comment.CannotDeleteCommentException.AlreadyDeletedException;
import com.threadly.posts.comment.CannotDeleteCommentException.BlockedException;
import com.threadly.posts.comment.CannotDeleteCommentException.ParentPostInactiveException;
import com.threadly.posts.comment.CannotDeleteCommentException.WriteMismatchException;
import com.threadly.util.RandomUtils;
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
  private PostCommentStatusType status;

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
        PostCommentStatusType.ACTIVE
    );
  }

  /**
   * 게시글 댓글 상태 DELETED로 변경
   */
  public void markAsDeleted() {
    /*상태 검증*/
    if (status == PostCommentStatusType.DELETED) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_ALREADY_DELETED);
    } else if (status == PostCommentStatusType.BLOCKED) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_DELETE_BLOCKED);
    }

    this.status = PostCommentStatusType.DELETED;
  }

  /**
   * 게시글 댓글이 삭제 가능 상태인지 검증
   * @param userId
   * @param postStatus
   */
  public void validateDeletableBy(String userId, PostStatusType postStatus ) {
    if (!this.userId.equals(userId)) {
      throw new WriteMismatchException();
    } else if(this.status == PostCommentStatusType.DELETED) {
      throw new AlreadyDeletedException();
    } else if(this.status == PostCommentStatusType.BLOCKED) {
      throw new BlockedException();
    } else if (postStatus != PostStatusType.ACTIVE) {
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
    if(this.status != PostCommentStatusType.ACTIVE) {
      throw new CannotLikePostCommentException();
    }

  }
  /**
   * 댓글 좋아요
   *
   * @param userId
   */
  public CommentLike like(String userId) {
    CommentLike newCommentLike = new CommentLike(this.commentId, userId);
    commentLikes.add(newCommentLike);
    return
        newCommentLike;
  }

  /**
   * 댓글 좋아요 취소
   *
   * @param userId
   */
  public void unlike(String userId) {
    commentLikes.remove(new CommentLike(this.commentId, userId));
  }


  /**
   * 매핑 전용 생성자
   * @param commentId
   * @param postId
   * @param userId
   * @param content
   * @param status
   */
  public PostComment(String commentId, String postId, String userId, String content,
      PostCommentStatusType status) {
    this.commentId = commentId;
    this.postId = postId;
    this.userId = userId;
    this.content = content;
    this.status = status;
  }

  /*Test*/
  @VisibleForTesting
  void setCommentId(String commentId) {
    this.commentId = commentId;
  }
}

