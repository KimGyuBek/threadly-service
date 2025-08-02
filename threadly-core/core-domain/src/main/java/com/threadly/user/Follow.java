package com.threadly.user;

import com.threadly.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Follow 도메인
 */
@Getter
@AllArgsConstructor
public class Follow {

  private String followId;
  private String followerId;
  private String followingId;
  private FollowStatusType statusType;
//  private LocalDateTime modifiedAt;


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
    this.statusType = FollowStatusType.APPROVED;
  }

  /**
   * userStatusType을 REJECTED로 변경
   */
  public void markAsRejected() {
    this.statusType = FollowStatusType.REJECTED;
  }
}
