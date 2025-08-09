package com.threadly.adapter.persistence.post.entity;


import com.threadly.adapter.persistence.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 댓글 좋아요 Entity
 */
@Table(name = "comment_likes")
@Entity
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CommentLikeEntity {

  @EmbeddedId
  private UserIdAndCommentId id;

  @MapsId("commentId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private PostCommentEntity comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @MapsId("userId")
  private UserEntity user;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public CommentLikeEntity() {}

}
