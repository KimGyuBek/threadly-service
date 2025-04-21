package com.threadly.verification;


import java.time.Duration;

public interface EmailVerificationPort {

  /**
   * code 저장
   *
   * @param userId
   * @param code
   */
  void saveCode(String userId, String code, Duration expiration);

  /**
   * code로 userId 조회
   *
   * @param code
   * @return
   */
  String getUserId(String code);


  /**
   * code 삭제
   *
   * @param code
   */
  void deleteCode(String code);


}
