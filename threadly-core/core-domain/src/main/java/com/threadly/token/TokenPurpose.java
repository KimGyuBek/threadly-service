package com.threadly.token;

/**
 * 토큰 목적
 */
public enum TokenPurpose {


  PASSWORD_REVERIFY, // 민감한 작업 전 비밀번호 재확인용
  FIRST_LOGIN_PROFILE // 회원가입 직후 첫 로그인 시 프로필 설정용
}
