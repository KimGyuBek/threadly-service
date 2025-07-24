package com.threadly.mapper.user;

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
    return
        User.builder()
            .userId(entity.getUserId())
            .userName(entity.getUserName())
            .password(entity.getPassword())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .userType(entity.getUserType())
            .isEmailVerified(entity.isEmailVerified())
            .isActive(entity.isActive())
            .build();
  }

//  public static User toDomain(UserEntity userEntity) {
//    User user;
//    user = User.builder()
//        .userId(userEntity.getUserId())
//        .userName(userEntity.getUserName())
//        .password(userEntity.getPassword())
//        .email(userEntity.getEmail())
//        .phone(userEntity.getPhone())
//        .userType(userEntity.getUserType())
//        .isEmailVerified(userEntity.isEmailVerified())
//        .isActive(userEntity.isActive())
//        .build();
//
//    if (userProfileEntity != null) {
//      user.setUserProfile(
//          UserProfileMapper.toDomain(userProfileEntity));
//    }
//
//    return user;
//  }

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


