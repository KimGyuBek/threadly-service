package com.threadly.entity.post;


import com.threadly.entity.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 댓글 좋아요 Entity
 */
@Table(name = "comment_likes")
@Entity
public class CommentLikesEntity {

  @EmbeddedId
  private UserIdAndCommentId id;

  @MapsId("commentId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private PostCommentsEntity comments;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @MapsId("userId")
  private UserEntity user;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
