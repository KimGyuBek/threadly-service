package com.threadly.core.service.token.processor;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.commons.utils.JwtTokenUtils;
import com.threadly.core.port.token.out.TokenCommandPort;
import com.threadly.core.port.token.out.command.InsertBlackListTokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Token 프로세서
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProcessor {

  private final TokenCommandPort tokenCommandPort;
  private final TtlProperties ttlProperties;

  public void addBlackListTokenAndDeleteRefreshToken(String userId, String barerToken) {
    /*토큰 추출*/
    String accessToken = JwtTokenUtils.extractAccessToken(barerToken);
    log.debug("accessToken: {}", accessToken);

    /*블랙리스트 토큰 등록*/
    tokenCommandPort.saveBlackListToken(InsertBlackListTokenCommand.builder()
        .userId(userId)
        .accessToken(accessToken)
        .duration(ttlProperties.getBlacklistToken())
        .build());
    log.debug("블랙리트스 토큰 등록 완료");

    /*refreshToken 삭제*/
    tokenCommandPort.deleteRefreshToken(userId);
    log.debug("리프레스 토큰 삭제 완료");
  }

}
