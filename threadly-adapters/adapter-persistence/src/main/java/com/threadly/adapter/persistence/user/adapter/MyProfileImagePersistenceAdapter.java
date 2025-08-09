package com.threadly.adapter.persistence.user.adapter;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.adapter.persistence.user.mapper.UserProfileImageMapper;
import com.threadly.adapter.persistence.user.repository.UserProfileImageJpaRepository;
import com.threadly.core.port.user.profile.image.CreateMyProfileImagePort;
import com.threadly.core.port.user.profile.image.FetchMyProfileImagePort;
import com.threadly.core.port.user.profile.image.UpdateMyProfileImagePort;
import com.threadly.core.domain.user.profile.image.UserProfileImage;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 사용자 프로필 이미지 관련 adapter
 */
@Repository
public class MyProfileImagePersistenceAdapter implements CreateMyProfileImagePort,
    UpdateMyProfileImagePort, FetchMyProfileImagePort {

  @Autowired
  private final UserProfileImageJpaRepository userProfileImageJpaRepository;

  public MyProfileImagePersistenceAdapter(
      UserProfileImageJpaRepository userProfileImageJpaRepository) {
    this.userProfileImageJpaRepository = userProfileImageJpaRepository;
  }

  @Override
  public void create(UserProfileImage domain) {
    userProfileImageJpaRepository.save(UserProfileImageMapper.toEntity(domain));
  }

  public void updateStatusById(String profileImageId, ImageStatus status) {
    userProfileImageJpaRepository.updateStatusById(profileImageId, status);
  }

  @Override
  public boolean existsNotDeletedByUserProfileImageId(String userProfileImageId) {
    return userProfileImageJpaRepository.existsNotDeletedByUserProfileImageId(userProfileImageId);
  }

  @Override
  public void updateStatusAndUserIdByImageId(String imageId, String userId, ImageStatus status) {
    userProfileImageJpaRepository.updateStatusAndUserIdByUserProfileImageId(imageId, userId,
        status);
  }

  @Override
  public Optional<String> findConfirmedProfileImageIdByUserId(String userId) {
    return userProfileImageJpaRepository.findConfirmedProfileImageIdByUserId(userId);
  }
}
