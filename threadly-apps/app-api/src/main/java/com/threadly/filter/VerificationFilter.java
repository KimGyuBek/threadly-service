package com.threadly.filter;

import com.threadly.ErrorCode;
import com.threadly.auth.JwtTokenProvider;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.token.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 사용자 이중 인증을 위한 filter
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return !path.startsWith("/api/users/update/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = resolveToken(request);

      /*토큰 검증*/
      jwtTokenProvider.validateToken(token);

    } catch (Exception e) {
      log.error(e.getMessage());
      UserAuthenticationException exception = new UserAuthenticationException(
          ErrorCode.SECOND_VERIFICATION_FAILED);

      request.setAttribute("exception", exception);
      throw exception;
    }

    filterChain.doFilter(request, response);

  }

  private String resolveToken(HttpServletRequest request) {

    /*header에서 token 가져오기*/
    String verifyToken = request.getHeader("X-Verify-Token");

    /*token이 존재하지 않을 경우*/
    if (verifyToken == null || !verifyToken.startsWith("Bearer ")) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }
    return
        verifyToken.substring(7);

  }
}
