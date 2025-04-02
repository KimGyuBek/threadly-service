package com.threadly.entity.user;

public enum UserType {
  USER("사용자"),
  SELLER("판매자"),
  ADMIN("관리자");

  private String value;

  UserType(final String value) {
    this.value = value;
  }
}


