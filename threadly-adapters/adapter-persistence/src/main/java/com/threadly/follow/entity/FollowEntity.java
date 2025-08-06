package com.threadly.follow.entity;

import com.threadly.base.BaseEntity;
import com.threadly.user.entity.UserEntity;
import com.threadly.follow.FollowStatusType;
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
import lombok.NoArgsConstructor;

@Table(name = "user_follows")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowEntity extends BaseEntity {

  @Id
  @Column(name = "follow_id")
  private String followId;

  @JoinColumn(name = "follower_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity follower;

  @JoinColumn(name = "following_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity following;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private FollowStatusType statusType;

}
