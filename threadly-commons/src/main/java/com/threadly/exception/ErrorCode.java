package com.threadly.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


  /*공통*/
  INTERNAL_SERVER_ERROR("TLY0000", "에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("TLY0001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  ACCESS_DENIED("TLY0002", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

  /*User*/
  USER_NOT_FOUND("TLY2000", "사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  USER_ALREADY_EXISTS("TLY2001", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  USER_INACTIVE("TLY2002", "비활성화된 사용자입니다.", HttpStatus.FORBIDDEN),
  INVALID_PASSWORD("TLY2003", "패스워드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ACCOUNT_DISABLED("TLY2004", "비활성화 된 계정입니다.", HttpStatus.FORBIDDEN),
  ACCOUNT_LOCKED("TLY2005", "잠긴 계정입니다.", HttpStatus.LOCKED),
  AUTHENTICATION_ERROR("TLY2006", "인증 에러", HttpStatus.UNAUTHORIZED),
  USER_AUTHENTICATION_FAILED("TLY2007", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  DUPLICATE_USER_NAME("TLY2008", "이미 사용 중인 사용자 이름입니다.", HttpStatus.CONFLICT),
  DUPLICATE_EMAIL("TLY2009", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
  PASSWORD_REQUIRED("TLY2010", "패스워드는 필수입니다.", HttpStatus.BAD_REQUEST),
  INVALID_USER_STATUS("TLY2011", "유효하지 않은 사용자 상태입니다.", HttpStatus.BAD_REQUEST),
  USER_ALREADY_DELETED("TLY2012", "이미 삭제된 사용자입니다.", HttpStatus.BAD_REQUEST),
  EMAIL_NOT_VERIFIED("TLY2013", "이메일 인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
  EMAIL_VERIFICATION_FAILED("TLY2014", "이메일 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
  SECOND_VERIFICATION_FAILED("TLY2015", "2차 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
  LOGIN_ATTEMPT_EXCEEDED("TLY2016", "로그인 시도 횟수를 초과하였습니다.", HttpStatus.TOO_MANY_REQUESTS),
  USER_PROFILE_NOT_FOUND("TLY2017", "사용자 프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),


  /*Token*/
  TOKEN_EXPIRED("TLY3000", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  TOKEN_INVALID("TLY3001", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
  TOKEN_MISSING("TLY3002", "토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

  /*Email*/
  EMAIL_SENDING_FAILED("TLY4000", "메일 전송중 오류 발생", HttpStatus.BAD_GATEWAY),
  EMAIL_CODE_NOT_PROVIDED("TLY4001", "인증 코드가 제공되지 않았습니다.", HttpStatus.BAD_REQUEST),
  EMAIL_CODE_INVALID("TLY4002", "유효하지 않은 인증 코드입니다.", HttpStatus.BAD_REQUEST),
  EMAIL_CODE_EXPIRED("TLY4003", "인증 코드가 만료되었습니다.", HttpStatus.GONE),
  EMAIL_ALREADY_VERIFIED("TLY4004", "이미 인증이 완료된 이메일입니다.", HttpStatus.CONFLICT),
  EMAIL_USER_NOT_FOUND("TLY4005", "해당 이메일에 대한 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  EMAIL_FORMAT_INVALID("TLY4006", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  EMAIL_REQUEST_INVALID("TLY4007", "잘못된 인증 요청입니다.", HttpStatus.BAD_REQUEST),

  /*Post*/
  POST_NOT_FOUND("TLY5000", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  POST_ALREADY_DELETED("TLY5001", "이미 삭제된 게시글입니다.", HttpStatus.BAD_REQUEST),
  POST_UPDATE_FORBIDDEN("TLY5002", "게시글을 수정할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_DELETE_FORBIDDEN("TLY5003", "게시글을 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_LIKE_DUPLICATED("TLY5004", "이미 좋아요를 누른 게시글입니다.", HttpStatus.CONFLICT),
  POST_LIKE_NOT_FOUND("TLY5005", "좋아요를 누르지 않은 게시글입니다.", HttpStatus.BAD_REQUEST),
  POST_DELETE_BLOCKED("TLY5006", "차단된 게시글은 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  POST_ALREADY_DELETED_ACTION("TLY5007", "이미 삭제된 게시글은 다시 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  POST_BLOCKED("TLY5008", "블라인드 된 게시글입니다.", HttpStatus.BAD_REQUEST),
  POST_ARCHIVED("TLY5009", "비공개 처리된 게시글입니다.", HttpStatus.BAD_REQUEST),
  POST_LIKE_NOT_ALLOWED("TLY5010", "삭제되었거나 차단된 게시글에는 좋아요를 누를 수 없습니다.",
      HttpStatus.BAD_REQUEST),
  POST_NOT_ACCESSIBLE("TLY5011", "이 게시글은 볼 수 없습니다.",
      HttpStatus.BAD_REQUEST),

  /*PostComment*/
  POST_COMMENT_NOT_FOUND("TLY5100", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  POST_COMMENT_ALREADY_DELETED("TLY5101", "이미 삭제된 댓글입니다.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_DELETE_FORBIDDEN("TLY5102", "댓글을 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_COMMENT_WRITE_FORBIDDEN("TLY5103", "댓글을 작성할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_COMMENT_CONTENT_EMPTY("TLY5104", "댓글 내용이 비어있습니다.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_LIKE_DUPLICATED("TLY5105", "이미 좋아요를 누른 댓글입니다.", HttpStatus.CONFLICT),
  POST_COMMENT_LIKE_NOT_FOUND("TLY5106", "좋아요를 누르지 않은 댓글입니다.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_DELETE_BLOCKED("TLY5107", "차단된 댓글은 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_PARENT_POST_INACTIVE("TLY5108", "댓글이 속한 게시글은 현재 수정/삭제할 수 없습니다.",
      HttpStatus.BAD_REQUEST),
  POST_COMMENT_LIKE_NOT_ALLOWED("TLY5109", "삭제되었거나 차단된 댓글에는 좋아요를 누를 수 없습니다.",
      HttpStatus.BAD_REQUEST),
  POST_COMMENT_BLOCKED("TLY5110", "차단된 댓글입니다.",
      HttpStatus.BAD_REQUEST),
  POST_COMMENT_DELETED("TLY5111", "삭제된 댓글입니다.",
      HttpStatus.BAD_REQUEST),
  POST_COMMENT_NOT_ACCESSIBLE("TLY5112", "이 댓글은 볼 수 없습니다.",
      HttpStatus.BAD_REQUEST),

  /*PostImage*/
  POST_IMAGE_NOT_FOUND("TLY5200", "이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  POST_IMAGE_UPLOAD_LIMIT_EXCEEDED("TLY5201", "최대 업로드 가능한 이미지 수를 초과했습니다.",
      HttpStatus.BAD_REQUEST),
  POST_IMAGE_INVALID_EXTENSION("TLY5202", "허용되지 않는 파일 확장자입니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_INVALID_MIME_TYPE("TLY5203", "유효하지 않은 이미지 MIME 타입입니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_TOO_LARGE("TLY5204", "이미지 파일 크기가 너무 큽니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_UPLOAD_FAILED("TLY5205", "이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  POST_IMAGE_OWNER_MISMATCH("TLY5206", "사용자에게 해당 게시글 이미지에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_IMAGE_ALREADY_ATTACHED("TLY5207", "이미 게시글에 첨부된 이미지입니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_TEMP_EXPIRED("TLY5208", "임시 업로드 이미지의 유효 시간이 만료되었습니다.", HttpStatus.GONE),
  POST_IMAGE_EMPTY("TLY5209", "게시글에는 최소 한 장의 이미지를 첨부해야 합니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_UPLOAD_FORBIDDEN("TLY5210", "게시글 이미지 업로드 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_IMAGE_INVALID_IMAGE("TLY5211", "잘못된 이미지 파일 입니다.", HttpStatus.FORBIDDEN),
  POST_IMAGE_EXTENSION_MISMATCH("TLY5212", "확장자가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  POST_IMAGE_ASPECT_RATIO_INVALID("TLY5213", "허용되지 않는 이미지 비율입니다.", HttpStatus.BAD_REQUEST);


  private final String code;
  private final String desc;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String desc, HttpStatus httpStatus) {
    this.code = code;
    this.desc = desc;
    this.httpStatus = httpStatus;
  }

}
