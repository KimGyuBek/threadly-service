package com.threadly.entity.post;


import com.threadly.entity.BaseEntity;
import com.threadly.entity.user.UserEntity;
import com.threadly.post.PostCommentStatusType;
import com.threadly.post.comment.PostComment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 댓글 Entity
 */
@Getter
@Table(name = "post_comments")
@Entity
@AllArgsConstructor
public class PostCommentEntity extends BaseEntity {

  @Id
  @Column(name = "comment_id")
  private String commentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "content")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private PostCommentStatusType status;

  /**
   * 새 댓글 생성
   *
   * @param postComment
   * @return
   */
  public static PostCommentEntity newComment(PostComment postComment
  ) {
    return new PostCommentEntity(
        postComment.getCommentId(),
        PostEntity.fromId(postComment.getPostId()),
        UserEntity.fromId(postComment.getUserId()),
        postComment.getContent(),
        postComment.getStatus()
    );
  }

  /**
   * 프록시 객체 생성
   *
   * @param commentId
   * @return
   */
  public static PostCommentEntity fromId(String commentId) {
    PostCommentEntity postCommentEntity = new PostCommentEntity();
    postCommentEntity.commentId = commentId;
    return postCommentEntity;
  }

  public PostCommentEntity() {
  }
}
