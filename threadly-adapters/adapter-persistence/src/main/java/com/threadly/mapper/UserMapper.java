package com.threadly.mapper;

import com.threadly.entity.user.UserEntity;
import com.threadly.user.User;

public class UserMapper {

  /**
   * entity -> domain
   *
   * @param entity
   * @return
   */
  public static User toDomain(UserEntity entity) {
    return new User(
        entity.getUserId(),
        entity.getUserName(),
        entity.getPassword(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getUserType(),
        entity.isActive(),
        entity.isEmailVerified()
    );
  }

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static UserEntity toEntity(User domain) {
    return UserEntity.newUser(
        domain.getUserName(),
        domain.getPassword(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getUserType(),
        domain.isActive(),
        domain.isEmailVerified()
    );
  }

}
