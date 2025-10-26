package com.threadly.user.controller.api;

import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.user.request.me.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "내 계정 API", description = "사용자 본인의 계정 관리 API")
public interface MyAccountApi {

  /**
   * 비밀번호 변경
   */
  @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", content = @Content),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 비밀번호 형식", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패 또는 현재 비밀번호 불일치", content = @Content)
  })
  @PatchMapping("/password")
  ResponseEntity<Void> changePassword(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "비밀번호 변경 요청 정보", required = true)
      @RequestBody ChangePasswordRequest request);

  /**
   * 계정 탈퇴
   */
  @Operation(summary = "계정 탈퇴", description = "사용자 계정을 영구적으로 삭제합니다. 이 작업은 되돌릴 수 없습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "계정 탈퇴 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @DeleteMapping("")
  ResponseEntity<Void> withdrawMyAccount(
      @Parameter(description = "Bearer 액세스 토큰", required = true)
      @RequestHeader("Authorization") String bearerToken,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

  /**
   * 계정 비활성화
   */
  @Operation(summary = "계정 비활성화", description = "계정을 일시적으로 비활성화합니다. 나중에 다시 활성화할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "계정 비활성화 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @PatchMapping("/deactivate")
  ResponseEntity<Void> deactivateMyAccount(
      @Parameter(description = "Bearer 액세스 토큰", required = true)
      @RequestHeader("Authorization") String bearerToken,
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user);

}
