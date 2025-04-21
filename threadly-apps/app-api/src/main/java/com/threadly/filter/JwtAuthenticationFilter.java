package com.threadly.filter;

import com.threadly.ErrorCode;
import com.threadly.auth.JwtTokenProvider;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.token.TokenException;
import com.threadly.exception.user.UserException;
import com.threadly.user.FetchUserUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Jwt 인증 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return
        FilterBypassMatcher.shouldBypass(request.getRequestURI());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = resolveToken(request);

      /*토큰이 검증되면*/
      if (jwtTokenProvider.validateToken(token)) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        /*인증*/
        SecurityContextHolder.getContext().setAuthentication(authentication);

      }

      /*사용자가 없는 경우*/
    } catch (UserException e) {
      UserAuthenticationException exception = new UserAuthenticationException(e.getErrorCode());
      request.setAttribute("exception", exception);
      throw exception;

    } catch (TokenException e) {
      request.setAttribute("exception", e);
      throw e;

    } catch (Exception e) {
      request.setAttribute("exception", e);
      throw e;
    }

    filterChain.doFilter(request, response);
  }

  private static String resolveToken(HttpServletRequest request) {
    /*authorization header 가져오기*/
    String bearerToken = request.getHeader("Authorization");

    /*bearer Token이 존재할 경우*/
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    /*존재하지 않을 경우*/
    throw new TokenException(ErrorCode.TOKEN_MISSING);
  }
}
