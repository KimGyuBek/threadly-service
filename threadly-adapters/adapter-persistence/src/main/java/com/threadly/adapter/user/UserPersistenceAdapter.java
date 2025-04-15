package com.threadly.adapter.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.mapper.UserMapper;
import com.threadly.repository.user.UserJpaRepository;
import com.threadly.user.FetchUserPort;
import com.threadly.user.InsertUserPort;
import com.threadly.user.UserEmailVerificationPort;
import com.threadly.user.User;
import com.threadly.user.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements FetchUserPort, InsertUserPort,
    UserEmailVerificationPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public Optional<User> findByEmail(String email) {

    /*userId로 user 조회*/
    Optional<UserEntity> byEmail = userJpaRepository.findByEmail(email);

    /*user가 존재하지 않을 경우*/
    if (byEmail.isEmpty()) {
      return Optional.empty();
    }

    /*user가 존재할 경우*/
    return byEmail.map(UserMapper::toDomain);

  }

  @Override
  public UserPortResponse create(User user) {
    UserEntity userEntity = UserMapper.toEntity(user);
    userJpaRepository.save(userEntity);

    return
        UserPortResponse.builder()
            .userId(userEntity.getUserId())
            .userName(userEntity.getUserName())
            .password(userEntity.getPassword())
            .email(userEntity.getEmail())
            .phone(userEntity.getPhone())
            .userType(userEntity.getUserType().name())
            .isActive(userEntity.isActive())
            .isEmailVerified(userEntity.isEmailVerified())
            .build();
  }


  @Override
  public Optional<User> findByUserId(String userId) {

    Optional<UserEntity> byId = userJpaRepository.findById(userId);

    return byId.map(UserMapper::toDomain);
  }

  @Override
  public void updateEmailVerification(User user) {

    userJpaRepository.updateEmailVerification(user.getUserId(), user.isEmailVerified());
  }
}
