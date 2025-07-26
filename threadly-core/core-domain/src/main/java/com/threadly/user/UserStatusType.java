package com.threadly.user;

/**
 * 사용자 상태 enum
 */
public enum UserStatusType {
  ACTIVE, //정상 회원
  INACTIVE, // 비활성화 상태
  DELETED, //탈퇴
  BANNED // 운영자에 의한 차단
}
