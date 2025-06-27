package com.threadly.post;

/**
 * 게시글 댓글 상태 enum
 */
public enum PostCommentStatus {
  ACTIVE("일반 노출 댓글"),
  DELETED("작성자가 삭제한 댓글"),
  BLOCKED("운영자에 의해 블라인드 처리됨(신고/정책 위반)");

  PostCommentStatus(String desc) {
    this.desc = desc;
  }

  private final String desc;
  }
