package com.threadly.user;

/**
 * 팔로우 상태
 */
public enum FollowStatusType {
  NONE, // 팔로우 요청도 안 한 상태
  PENDING, //팔로우 요청이 수락 대기 중인 상태 (비공개 계정 대상)
  APPROVED, // 팔로우 요청이 수락된 상태 (팔로잉 관계 성립)
  SELF // 내 프로필인 경우
}
