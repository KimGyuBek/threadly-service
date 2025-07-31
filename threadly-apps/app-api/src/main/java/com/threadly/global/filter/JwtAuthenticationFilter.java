package com.threadly.global.filter;

import com.google.common.base.Objects;
import com.threadly.auth.AuthManager;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.token.TokenException;
import com.threadly.exception.user.UserException;
import com.threadly.global.exception.TokenAuthenticationException;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.security.JwtTokenProvider;
import com.threadly.user.UserStatusType;
import com.threadly.utils.JwtTokenUtils;
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

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  private final AuthManager authManager;


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return
        FilterBypassMatcher.shouldBypass(request.getRequestURI());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = JwtTokenUtils.extractAccessToken(request.getHeader("Authorization"));

      /*blacklist token 조회 후 있을경우 예외 처리*/
      if (authManager.isBlacklisted(token)) {
        throw new TokenException(ErrorCode.TOKEN_INVALID);
      }

      /*토큰이 검증되면*/
      if (jwtTokenProvider.validateToken(token)) {

        /*UserStatusType 검증*/
        String userStatusType = jwtTokenProvider.getUserStatusType(token);

        /*INCOMPLETE_PROFILE*/
        if (Objects.equal(userStatusType,
            UserStatusType.INCOMPLETE_PROFILE.name()) && !isWhiteListedForProfileIncomplete(
            request.getRequestURI())) {
          throw new UserException(ErrorCode.USER_PROFILE_NOT_SET);

          /*INACTIVE*/
        } else if(Objects.equal(userStatusType, UserStatusType.INACTIVE.name())) {
          throw new UserException(ErrorCode.USER_INACTIVE);
        }

        /*TODO 성능 부하*/
        Authentication authentication = authManager.getAuthentication(token);

        /*인증*/
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      /*사용자가 관련 오류 */
    } catch (UserException e) {
      UserAuthenticationException exception = new UserAuthenticationException(e.getErrorCode());
      authenticationEntryPoint.commence(request, response, exception);
      return;

    } catch (TokenException e) {
      TokenAuthenticationException exception = new TokenAuthenticationException(e.getErrorCode());
      authenticationEntryPoint.commence(request, response, exception);
      return;

    } catch (Exception e) {
      throw e;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * 프로필 설정용 white list
   *
   * @param uri
   * @return
   */
  private boolean isWhiteListedForProfileIncomplete(String uri) {
    return uri.equals("/api/me/profile");
  }
}
