package com.threadly.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.ErrorCode;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.token.TokenException;
import com.threadly.response.ApiResponse;
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

    Throwable exception = (Throwable) request.getAttribute("exception");
    response.setContentType("application/json; charset=utf-8");

    /*TokenException일경우*/
    if (exception instanceof TokenException) {
      ErrorCode errorCode = ((TokenException) exception).getErrorCode();

      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      /*나머지 예외*/
    } else if (exception instanceof UserAuthenticationException) {
      ErrorCode errorCode = ((UserAuthenticationException) exception).getErrorCode();
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));


    } else {
      ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

    }

  }
}
