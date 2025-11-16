package com.threadly.auth.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 API", description = "사용자 인증 VIEW 관련 API")
public interface AuthViewApi {

  /**
   * 이메일 인증
   */
  @Operation(summary = "이메일 인증", description = "회원가입 시 전송된 이메일의 인증 코드로 이메일을 인증합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "이메일 인증 성공", content = @Content),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드", content = @Content),
      @ApiResponse(responseCode = "404", description = "인증 코드를 찾을 수 없음", content = @Content)
  })
  @GetMapping("/verify-email")
  String verifyMail(
      @Parameter(description = "이메일 인증 코드", required = true, example = "abc123xyz")
      @RequestParam String code);
}
