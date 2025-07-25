package com.threadly.user.profile;

/**
 * 사용자 계정 타입
 */
public enum UserProfileType {
  USER("일반 사용자"),
  PROFESSIONAL("비즈니스");

  private String desc;

  UserProfileType(String desc) {
    this.desc = desc;
  }
}
