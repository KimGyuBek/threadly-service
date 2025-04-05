package com.threadly.adapter.token;

import com.threadly.entity.token.TokenEntity;
import com.threadly.mapper.TokenMapper;
import com.threadly.repository.token.TokenJpaRepository;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.Token;
import com.threadly.token.UpdateTokenPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenPersistenceAdapter implements FetchTokenPort, UpdateTokenPort {

  private final TokenJpaRepository tokenJpaRepository;


  @Override
  public Optional<Token> findByUserId(String userId) {
    Optional<TokenEntity> byUserId = tokenJpaRepository.findByUserId(userId);

    if (byUserId.isPresent()) {
      return byUserId.map(TokenMapper::toDomain);
    }

    return Optional.empty();
  }

  @Override
  public Optional<String> findUserIdByAccessToken(String accessToken) {
    return
        tokenJpaRepository.findUserIdByAccessToken(accessToken);
  }

  @Override
  public Token upsertToken(Token token) {

    /*userId로 토큰 조회*/
    Optional<TokenEntity> result = tokenJpaRepository.findByUserId(token.getUserId());

    /*토큰이 있으면*/
    if (result.isPresent()) {
      TokenEntity tokenEntity = result.get();

      /*token entity 값 업데이트*/
      tokenEntity.updateToken(
          token.getAccessToken(),
          token.getRefreshToken(),
          token.getAccessTokenExpiresAt(),
          token.getRefreshTokenExpiresAt()
      );

      System.out.println("토큰 업데이트 :" + tokenEntity.toString());

    } else {
      /*토큰이 없으면*/
      /*새로운 토큰 생성*/
      TokenEntity tokenEntity = TokenEntity.newTokenEntity(
          token.getUserId(),
          token.getAccessToken(),
          token.getRefreshToken(),
          token.getUserType(),
          token.getAccessTokenExpiresAt(),
          token.getRefreshTokenExpiresAt()
      );

      /*token save*/
      tokenJpaRepository.save(tokenEntity);

      System.out.println("새로운 토큰 생성 :" + tokenEntity.toString());
    }

    return token;
  }
}
