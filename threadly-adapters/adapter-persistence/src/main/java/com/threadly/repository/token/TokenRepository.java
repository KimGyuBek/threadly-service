package com.threadly.repository.token;

import com.threadly.entity.token.TokenEntity;
import com.threadly.token.CreateToken;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.UpdateToken;
import com.threadly.token.UpdateTokenPort;
import com.threadly.token.response.TokenPortResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository

@RequiredArgsConstructor
public class TokenRepository implements FetchTokenPort, InsertTokenPort , UpdateTokenPort {

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  public Optional<TokenPortResponse> findByUserId(String userId) {

    return
        tokenJpaRepository.findByUserId(userId)
            .map(TokenEntity::toTokenPortResponse);
  }

  @Override
  public TokenPortResponse create(CreateToken createToken) {
    LocalDateTime now = LocalDateTime.now();
    return
        tokenJpaRepository.save(
            TokenEntity.newTokenEntity(
                createToken.getUserId(),
                createToken.getAccessToken(),
                createToken.getRefreshToken(),
                createToken.getUserType(),
                createToken.getAccessTokenExpiresAt(),
                createToken.getRefreshTokenExpiresAt()
            )).toTokenPortResponse();


  }

  @Override
  public void updateToken(UpdateToken updateToken) {
    new TokenEntity(
//        updateToken.get


    );



  }
}
