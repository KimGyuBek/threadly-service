package com.threadly.post;

/**
 * 게시글 상태 enum
 */
public enum PostStatus {
  ACTIVE("일반 노출 게시글"),
  DELETED("작성자가 삭제한 게시글"),
  BLOCKED("운영자에 의해 블라인드 처리됨(신고/정책 위반)"),
  ARCHIVE("사용자가 임의로 숨긴 게시글");

  PostStatus(String desc) {
    this.desc = desc;
  }

  private final String desc;
}
