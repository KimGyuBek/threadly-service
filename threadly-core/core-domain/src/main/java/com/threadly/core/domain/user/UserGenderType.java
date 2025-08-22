package com.threadly.core.domain.user;

/**
 * 사용자 성별 타입
 */
public enum UserGenderType {
  MALE("남성"),
  FEMALE("여성"),
  ETC("기타");

  private String desc;

  UserGenderType(String desc) {
    this.desc = desc;
  }
}
