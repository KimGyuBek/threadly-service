package com.threadly.auth.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.threadly.annotation.PasswordEncryption;
import java.sql.ConnectionBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 정보 변경을 위한 이중인증 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordVerificationRequest {

  private String password;


}
