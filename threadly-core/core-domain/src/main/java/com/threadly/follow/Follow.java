package com.threadly.follow;

import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Follow 도메인
 */
@Getter
@AllArgsConstructor
@Builder
public class Follow {

  private String followId;
  private String followerId;
  private String followingId;
  private FollowStatusType statusType;


  /**
   * 새로운 팔로우 관계 생성
   *
   * @param userId
   * @param targetUserId
   * @return
   */
  public static Follow createFollow(String userId, String targetUserId) {
    return new Follow(
        RandomUtils.generateNanoId(),
        userId,
        targetUserId,
        FollowStatusType.PENDING
    );
  }

  /**
   * userStatusType을 APPROVED로 변경
   */
  public void markAsApproved() {
    if (this.statusType.equals(FollowStatusType.PENDING)) {
      this.statusType = FollowStatusType.APPROVED;
    }
  }

  /**
   * userStatusType을 SELF 변경
   */
  public void markAsSelf() {
    this.statusType = FollowStatusType.SELF;
  }
}
