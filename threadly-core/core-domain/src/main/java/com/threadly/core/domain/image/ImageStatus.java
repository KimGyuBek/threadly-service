package com.threadly.core.domain.image;

/**
 * 게시글 이미지 상태
 */
public enum ImageStatus {

  TEMPORARY("임시 업로드된 상태"),
  CONFIRMED("확정 상태"),
  DELETED("삭제된 상태");

  private String desc;

  ImageStatus(String desc) {
    this.desc = desc;
  }

}
