package com.threadly.post.comment;

/**
 * 게시글 댓글 삭제 불가 예외
 */
public class CannotDeleteCommentException extends RuntimeException{

  /**
   * 작성자 불일치
   */
  public static class WriteMismatchException extends CannotDeleteCommentException { }

  /**
   * 이미 삭제된 댓글
   */
  public static class AlreadyDeletedException extends CannotDeleteCommentException { }

  /**
   * 차단된 댓글
   */
  public static class BlockedException extends CannotDeleteCommentException { }

  /**
   * 댓글이 속한 게시글이 삭제 불가 상태
   */
  public static class ParentPostInactiveException extends CannotDeleteCommentException { }

}
