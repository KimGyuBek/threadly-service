package com.threadly.adapter.persistence.user.mapper;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.user.User;

public class UserMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static User toDomain(UserEntity entity) {
    return
        User.builder()
            .userId(entity.getUserId())
            .userName(entity.getUserName())
            .password(entity.getPassword())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .userRoleType(entity.getUserRoleType())
            .isEmailVerified(entity.isEmailVerified())
            .userStatus(entity.getUserStatus())
            .isPrivate(entity.isPrivate())
            .build();
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static UserEntity toEntity(User domain) {
    return
        UserEntity.newUser(
            domain
        );
  }
}


