package com.threadly.adapter.user;


import com.threadly.mapper.user.UserProfileMapper;
import com.threadly.repository.user.UserProfileJpaRepository;
import com.threadly.user.UserProfile;
import com.threadly.user.profile.save.SaveUserProfilePort;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfilePersistenceAdapter implements SaveUserProfilePort {

  private final UserProfileJpaRepository userProfileJpaRepository;

  public UserProfilePersistenceAdapter(UserProfileJpaRepository userProfileJpaRepository) {
    this.userProfileJpaRepository = userProfileJpaRepository;
  }

  @Override
  public void saveUserProfile(UserProfile userProfile) {
    userProfileJpaRepository.save(
        UserProfileMapper.toEntity(userProfile)
    );
  }
}
