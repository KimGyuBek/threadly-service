package com.threadly.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.global.exception.TokenAuthenticationException;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.commons.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * SpringSecurty Authentication 인증 오류 핸들링
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=utf-8");

    /*TokenAuthenticationException일 경우*/
    if (authException instanceof TokenAuthenticationException) {
      ErrorCode errorCode = ((TokenAuthenticationException) authException).getErrorCode();

      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      return;
    }

    /*UserAuthenticationException일 경우*/
    if (authException instanceof UserAuthenticationException) {
      ErrorCode errorCode = ((UserAuthenticationException) authException).getErrorCode();
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      return;
      /*나머지 예외*/
    }

      ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

    return;
  }
}
