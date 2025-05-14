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

@Table(name = "post_likes")
@Entity
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

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
