package com.threadly.user.adapter;

import com.threadly.user.entity.UserEntity;
import com.threadly.user.mapper.UserMapper;
import com.threadly.user.repository.UserJpaRepository;
import com.threadly.user.FetchUserPort;
import com.threadly.user.SaveUserPort;
import com.threadly.user.UpdateUserPort;
import com.threadly.user.User;
import com.threadly.user.UserStatusType;
import com.threadly.user.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements FetchUserPort, SaveUserPort,
    UpdateUserPort {

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
  public void updateUserStatus(String userId, UserStatusType status) {
    userJpaRepository.updateStatus(userId, status);
  }

  @Override
  public void updateUserPhone(String userId, String phone) {
    userJpaRepository.updatePhoneByUserId(userId, phone);
  }

  @Override
  public void changePassword(String userId, String newPassword) {
    userJpaRepository.updatePasswordByUserId(userId, newPassword);
  }

  @Override
  public void updatePrivacy(User user) {
    userJpaRepository.updatePrivacyByUserId(user.getUserId(), user.isPrivate());
  }

  @Override
  public boolean isUserPrivate(String userId) {
    return userJpaRepository.isUserPrivate(userId);
  }
}
