package com.threadly.adapter.persistence.user.adapter;


import com.threadly.adapter.persistence.user.mapper.UserProfileMapper;
import com.threadly.adapter.persistence.user.repository.UserProfileJpaRepository;
import com.threadly.core.domain.user.profile.UserProfile;
import com.threadly.core.port.user.out.profile.UserProfileCommandPort;
import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserPreviewProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileCommandQueryPersistenceAdapter implements UserProfileQueryPort,
    UserProfileCommandPort {

  private final UserProfileJpaRepository userProfileJpaRepository;

  public UserProfileCommandQueryPersistenceAdapter(
      UserProfileJpaRepository userProfileJpaRepository) {
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

  @Override
  public void updateMyProfile(UserProfile userProfile) {
    userProfileJpaRepository.updateMyProfile(userProfile.getUserId(), userProfile.getNickname(),
        userProfile.getStatusMessage(), userProfile.getBio());
  }

}
