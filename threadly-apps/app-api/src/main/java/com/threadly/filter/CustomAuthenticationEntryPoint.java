package com.threadly.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.ErrorCode;
import com.threadly.exception.authentication.UserAuthErrorType;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.token.TokenErrorType;
import com.threadly.exception.token.TokenException;
import com.threadly.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
      TokenErrorType errorType = ((TokenException) exception).getErrorType();

      ErrorCode errorCode =
          switch (errorType) {
            case EXPIRED -> ErrorCode.TOKEN_EXPIRED;
            case INVALID -> ErrorCode.TOKEN_INVALID;
            case UNSUPPORTED -> ErrorCode.TOKEN_MISSING;
          };

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      /*나머지 예외*/
    } else if (exception instanceof UserAuthenticationException) {
      UserAuthErrorType errorType = ((UserAuthenticationException) exception).getUserAuthErrorType();

      HttpStatusCode httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;

      ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;
          switch (errorType) {
            case NOT_FOUND, INVALID_PASSWORD -> {
              errorCode = ErrorCode.USER_AUTHENTICATION_FAILED;
              httpStatusCode = HttpStatus.UNAUTHORIZED;
            }
            case ACCOUNT_DISABLED -> {
              errorCode = ErrorCode.ACCOUNT_DISABLED;
              httpStatusCode = HttpStatus.FORBIDDEN;
            }
            case ACCOUNT_LOCKED -> {
              errorCode = ErrorCode.ACCOUNT_LOCKED;
              httpStatusCode = HttpStatus.FORBIDDEN;
            }
            case AUTHENTICATION_ERROR -> {
              errorCode = ErrorCode.AUTHENTICATION_ERROR;
              httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            }
          };

      response.setStatus(httpStatusCode.value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));


    } else {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));

    }

  }
}
