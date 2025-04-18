//package com.threadly.token;
//
//import com.threadly.auth.token.FetchTokenUseCase;
//import com.threadly.auth.token.response.TokenResponse;
//import com.threadly.auth.token.response.UpdateTokenUseCase;
//import java.util.Optional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * TokenService
// */
//@Service
//@RequiredArgsConstructor
//public class TokenService implements FetchTokenUseCase, UpdateTokenUseCase {
//
//  private final FetchTokenPort fetchTokenPort;
//  private final UpdateTokenPort updateTokenPort;
//
//  @Transactional
//  @Override
//  public TokenResponse upsertToken(String userId, String accessToken, String refreshToken) {
//
//    Token token = Token.upsertToken(userId, accessToken, refreshToken);
//    updateTokenPort.upsertToken(token);
//
//    return TokenResponse.builder()
//        .accessToken(accessToken)
//        .refreshToken(refreshToken)
//        .build();
//
//  }
//
//  @Override
//  public String findUserIdByAccessToken(String accessToken) {
//    /*userId 조회*/
//    Optional<String> userId = fetchTokenPort.findUserIdByAccessToken(accessToken);
//
//    /*존재할 경우*/
//    if (userId.isPresent()) {
//      return userId.get();
//    }
//    throw new RuntimeException("Token not found");
//  }
//
//
//}
