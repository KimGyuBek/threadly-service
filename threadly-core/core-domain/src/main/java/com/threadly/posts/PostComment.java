package com.threadly.posts;

import com.google.common.annotations.VisibleForTesting;
import com.threadly.util.RandomUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 댓글
 */
@Getter
@Builder
public class PostComment {

  private String commentId;
  private String postId;
  private String userId;
  private String content;
  private PostCommentStatusType status;

  private Set<CommentLike> commentLikes;

  private PostComment(String commentId, String postId, String userId, String content,
      PostCommentStatusType status,
      Set<CommentLike> commentLikes) {
    this.commentId = commentId;
    this.postId = postId;
    this.userId = userId;
    this.content = content;
    this.status = status;
    this.commentLikes = new HashSet<>();
  }

  /**
   * 새로운 댓글 생성
   *
   * @param postId
   * @param userId
   * @param content
   * @return
   */
  public static PostComment newComment(String postId, String userId, String content) {
    return PostComment.builder()
        .commentId(RandomUtils.generateNanoId())
        .postId(postId)
        .userId(userId)
        .content((content != null) ? content : "")
        .status(PostCommentStatusType.ACTIVE)
        .commentLikes(new HashSet<>())
        .build();
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

  /*Test*/
  @VisibleForTesting
  void setCommentId(String commentId) {
    this.commentId = commentId;
  }
}

