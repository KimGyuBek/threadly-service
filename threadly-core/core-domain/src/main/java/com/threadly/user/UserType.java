package com.threadly.user;

public enum UserType {
  USER("사용자"),
  SELLER("판매자"),
  ADMIN("관리자");

  private String desc;


  UserType(final String value) {
    this.desc = value;
  }

  public String getDesc() {
    return desc;
  }
}


