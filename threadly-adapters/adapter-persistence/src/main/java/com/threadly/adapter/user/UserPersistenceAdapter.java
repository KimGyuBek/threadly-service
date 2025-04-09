package com.threadly.adapter.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.mapper.UserMapper;
import com.threadly.repository.user.UserJpaRepository;
import com.threadly.user.FetchUserPort;
import com.threadly.user.InsertUserPort;
import com.threadly.user.User;
import com.threadly.user.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements FetchUserPort, InsertUserPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public Optional<UserPortResponse> findByEmail(String email) {
    return
        userJpaRepository.findByEmail(email)
            .map(UserEntity::toUserPortResponse);
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
  public Optional<UserPortResponse> findByUserId(String userId) {
    return
        userJpaRepository.findById(userId).map(UserEntity::toUserPortResponse);
  }
}
