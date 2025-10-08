package com.threadly.core.domain.user;

public enum UserRoleType {
  USER("사용자"),
  ADMIN("관리자");

  private String desc;

  UserRoleType(final String value) {
    this.desc = value;
  }

  public String getDesc() {
    return desc;
  }
}


