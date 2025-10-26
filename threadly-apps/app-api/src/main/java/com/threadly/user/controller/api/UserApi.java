package com.threadly.user.controller.api;

import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.user.request.UserRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원 API", description = "회원 가입 관련 API")
public interface UserApi {

  /**
   * 회원 가입
   */
  @Operation(summary = "회원 가입", description = "새로운 사용자 계정을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원 가입 성공",
          content = @Content(schema = @Schema(implementation = RegisterUserApiResponse.class))),
      @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 데이터", content = @Content),
      @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일", content = @Content)
  })
  @PostMapping("")
  ResponseEntity<RegisterUserApiResponse> register(
      @Parameter(description = "회원 가입 요청 정보", required = true)
      @Valid @RequestBody UserRegisterRequest request);

}
