package com.threadly.adapter.user;

import com.threadly.mapper.user.UserProfileImageMapper;
import com.threadly.repository.user.UserProfileImageJpaRepository;
import com.threadly.user.profile.image.CreateUserProfileImagePort;
import com.threadly.user.profile.image.UserProfileImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 사용자 프로필 이미지 관련 adapter
 */
@Repository
public class UserProfileImagePersistenceAdapter implements CreateUserProfileImagePort {

  @Autowired
  private final UserProfileImageJpaRepository userProfileImageJpaRepository;

  public UserProfileImagePersistenceAdapter(
      UserProfileImageJpaRepository userProfileImageJpaRepository) {
    this.userProfileImageJpaRepository = userProfileImageJpaRepository;
  }

  @Override
  public void create(UserProfileImage domain) {
    userProfileImageJpaRepository.save(UserProfileImageMapper.toEntity(domain));
  }
}
