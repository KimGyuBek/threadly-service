package com.threadly.repository.token;

import lombok.RequiredArgsConstructor;

//@Repository

@RequiredArgsConstructor
public class TokenRepository {

//  private final TokenJpaRepository tokenJpaRepository;
//
//  @Override
//  public Optional<TokenPortResponse> findByUserId(String userId) {
//
//    return
//        tokenJpaRepository.findByUserId(userId)
//            .map(TokenEntity::toTokenPortResponse);
//  }
//
//  @Override
//  public TokenPortResponse create(CreateToken createToken) {
//    LocalDateTime now = LocalDateTime.now();
//    return
//        tokenJpaRepository.save(
//            TokenEntity.newTokenEntity(
//                createToken.getUserId(),
//                createToken.getAccessToken(),
//                createToken.getRefreshToken(),
//                createToken.getUserType(),
//                createToken.getAccessTokenExpiresAt(),
//                createToken.getRefreshTokenExpiresAt()
//            )).toTokenPortResponse();
//
//
//  }
//
//  @Override
//  public void updateToken(UpdateToken updateToken) {
//    new TokenEntity(
////        updateToken.get
//
//
//    );
//


//  }
}
