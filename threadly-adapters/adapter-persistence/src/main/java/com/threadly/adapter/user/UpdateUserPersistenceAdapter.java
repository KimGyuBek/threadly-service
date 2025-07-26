package com.threadly.adapter.user;

import com.threadly.entity.user.UserEntity;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.mapper.user.UserMapper;
import com.threadly.mapper.user.UserProfileMapper;
import com.threadly.repository.user.UserJpaRepository;
import com.threadly.repository.user.UserProfileJpaRepository;
import com.threadly.user.FetchUserPort;
import com.threadly.user.SaveUserPort;
import com.threadly.user.UpdateUserPort;
import com.threadly.user.User;
import com.threadly.user.UserStatusType;
import com.threadly.user.profile.UserProfile;
import com.threadly.user.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateUserPersistenceAdapter implements FetchUserPort, SaveUserPort,
    UpdateUserPort {

  private final UserJpaRepository userJpaRepository;
  private final UserProfileJpaRepository userProfileJpaRepository;

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
  public UserPortResponse save(User user) {
    UserEntity userEntity = UserMapper.toEntity(user);
    userJpaRepository.save(userEntity);

    return
        UserPortResponse.builder()
            .userId(userEntity.getUserId())
            .userName(userEntity.getUserName())
            .password(userEntity.getPassword())
            .email(userEntity.getEmail())
            .phone(userEntity.getPhone())
            .userType(userEntity.getUserType())
            .userStatusType(userEntity.getUserStatusType())
            .isEmailVerified(userEntity.isEmailVerified())
            .build();
  }


  @Override
  public Optional<User> findByUserId(String userId) {
    return userJpaRepository.findById(userId).map(
        UserMapper::toDomain);
  }

  @Override
  public void updateEmailVerification(String userId, boolean isEmailVerified) {
    userJpaRepository.updateEmailVerification(userId, isEmailVerified);
  }

  @Override
  public User findUserWithProfile(String userId) {
    UserProfile userProfile = UserProfileMapper.toDomain(
        userProfileJpaRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode.USER_PROFILE_NOT_FOUND)));
    return User.builder()
        .userId(userId)
        .userProfile(userProfile)
        .build();
  }

  @Override
  public void updateUserStatus(String userId, UserStatusType status) {
   userJpaRepository.updateStatus(userId, status);
  }
}
