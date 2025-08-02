package com.threadly.user;

/**
 * 팔로우 상태
 */
public enum FollowStatusType {
  PENDING, //팔로우 요청이 수락 대기 중인 상태 (비공개 계정 대상)
  APPROVED, // 팔로우 요청이 수락된 상태 (팔로잉 관계 성립)
  REJECTED // 팔로우 요청이 거절되 상태
}
