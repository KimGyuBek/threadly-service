package com.threadly.post;

/**
 * 게시글 이미지 상태
 */
public enum PostImageStatus {

  TEMPORARY("임시 업로드된 상태"),
  CONFIRMED("확정 상태"),
  DELETED("삭제된 상태");

  private String desc;

  PostImageStatus(String desc) {
    this.desc = desc;
  }

}
