package com.threadly.entity.post;


import com.threadly.entity.user.UserEntity;
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

@Table(name = "post_likes")
@Entity
@Getter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostLikeEntity {

  @EmbeddedId
  private PostIdAndUserId id;

  @MapsId("postId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @MapsId("userId")
  private UserEntity user;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public PostLikeEntity() {}
}
