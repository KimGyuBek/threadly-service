package com.threadly.adapter.user;


import com.threadly.mapper.user.UserProfileMapper;
import com.threadly.repository.user.UserProfileJpaRepository;
import com.threadly.user.profile.UserProfile;
import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.fetch.MyProfileDetailsProjection;
import com.threadly.user.profile.fetch.UserPreviewProjection;
import com.threadly.user.profile.fetch.UserProfileProjection;
import com.threadly.user.profile.save.SaveUserProfilePort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfilePersistenceAdapter implements FetchUserProfilePort, SaveUserProfilePort {

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

  @Override
  public void findByUserId(String userId) {
    //
  }

  @Override
  public UserPreviewProjection findUserPreviewByUserId(String userId) {
    return userProfileJpaRepository.findUserCommentPreviewByUserId(userId);
  }

  @Override
  public boolean existsUserProfileByUserId(String userId) {
    return
        userProfileJpaRepository.existsById(userId);
  }

  @Override
  public boolean existsByNickname(String nickname) {
    return userProfileJpaRepository.existsByNickname(nickname);
  }

  @Override
  public Optional<UserProfileProjection> findUserProfileByUserId(String userId) {
    return userProfileJpaRepository.findUserProfileByUserId(userId);
  }

  @Override
  public Optional<MyProfileDetailsProjection> findMyProfileDetailsByUserId(String userId) {
    return userProfileJpaRepository.findMyProfileDetailsByUserId(userId);
  }
}
