package com.threadly.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.core.domain.follow.Follow;
import com.threadly.core.domain.follow.FollowStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Follow 도메인 테스트
 */
class FollowTest {

  private Follow follow;

  /**
   * createFollow()
   */
  /*[Case #1] 팔로우 생성시 PENDING 상태로 생성되어야 한다*/
  @DisplayName("createFollow - 팔로우가 PENDING 상태로 생성되어야 한다")
  @Test
  public void createFollow_shouldCreateFollowWithPendingStatus() throws Exception {
    //given
    String userId = "user1";
    String targetUserId = "user2";

    //when
    follow = Follow.createFollow(userId, targetUserId);

    //then
    assertAll(
        () -> assertThat(follow.getFollowerId()).isEqualTo(userId),
        () -> assertThat(follow.getFollowingId()).isEqualTo(targetUserId),
        () -> assertThat(follow.getStatusType()).isEqualTo(FollowStatus.PENDING)
    );
  }

  /**
   * markAsApproved()
   */
  /*[Case #1] PENDING 상태에서 APPROVED로 변경되어야 한다*/
  @DisplayName("markAsApproved - PENDING 상태에서 APPROVED로 변경되어야 한다")
  @Test
  public void markAsApproved_shouldChangeStatusToApproved() throws Exception {
    //given
    generateFollow();

    //when
    follow.markAsApproved();

    //then
    assertThat(follow.getStatusType()).isEqualTo(FollowStatus.APPROVED);
  }

  /*[Case #2] PENDING이 아닌 상태에서는 상태가 변경되지 않아야 한다*/
  @DisplayName("markAsApproved - SELF 상태에서 변경되지 않아야 한다")
  @Test
  public void markAsApproved_shouldNotChange_whenNotPending() throws Exception {
    //given
    generateFollow();
    follow.markAsSelf();

    //when
    follow.markAsApproved();

    //then
    assertThat(follow.getStatusType()).isEqualTo(FollowStatus.SELF);
  }

  /**
   * markAsSelf()
   */
  /*[Case #1] SELF 상태로 변경되어야 한다*/
  @DisplayName("markAsSelf - SELF 상태로 변경되어야 한다")
  @Test
  public void markAsSelf_shouldChangeStatusToSelf() throws Exception {
    //given
    generateFollow();

    //when
    follow.markAsSelf();

    //then
    assertThat(follow.getStatusType()).isEqualTo(FollowStatus.SELF);
  }

  /*팔로우 생성*/
  private void generateFollow() {
    follow = Follow.createFollow("user1", "user2");
  }
}
