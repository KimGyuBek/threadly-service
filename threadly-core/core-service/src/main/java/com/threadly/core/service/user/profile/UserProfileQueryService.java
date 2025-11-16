package com.threadly.core.service.user.profile;

import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.user.in.profile.query.UserProfileQueryUseCase;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
import com.threadly.core.port.user.out.profile.projection.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import com.threadly.core.service.follow.validator.FollowValidator;
import com.threadly.core.service.user.validator.UserProfileValidator;
import com.threadly.core.service.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * userprofile 관련 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileQueryService implements UserProfileQueryUseCase {

  private final FollowValidator followValidator;
  private final UserValidator userValidator;
  private final UserProfileValidator userProfileValidator;

  @Transactional(readOnly = true)
  @Override
  public void validateNicknameUnique(String nickname) {
    userProfileValidator.validateNicknameDuplicate(nickname);
  }

  @Transactional(readOnly = true)
  @Override
  public GetUserProfileApiResponse getUserProfile(String userId, String targetUserId) {
    UserProfileProjection userProfileProjection = userProfileValidator.getUserProfileProjectionOrElseThrow(
        targetUserId);

    /*사용자 상태 검증*/
    userValidator.validateUserStatusWithException(userProfileProjection.getUserStatus());

    /*팔로우 유무 검증*/
    FollowStatus followStatus = followValidator.validateProfileAccessible(userId,
        targetUserId);

    return new GetUserProfileApiResponse(
        new UserPreview(
            userProfileProjection.getUserId(),
            userProfileProjection.getNickname(),
            userProfileProjection.getProfileImageUrl()
        ),
        userProfileProjection.getStatusMessage(),
        userProfileProjection.getBio(),
        followStatus
    );
  }

  @Transactional(readOnly = true)
  @Override
  public GetMyProfileDetailsApiResponse getMyProfileDetails(String userId) {
    /*사용자 상태 검증*/
    MyProfileDetailsProjection myProfileDetailsProjection = userProfileValidator.getMyProfileDetailsProjectionOrElseThrow(
        userId);

    /*사용자 상태 검증*/
    userValidator.validateMyStatusWithException(myProfileDetailsProjection.getStatus());

    /*응답 생성 후 리턴*/
    return new GetMyProfileDetailsApiResponse(
        userId,
        myProfileDetailsProjection.getNickname(),
        myProfileDetailsProjection.getStatusMessage(),
        myProfileDetailsProjection.getBio(),
        myProfileDetailsProjection.getPhone(),
        myProfileDetailsProjection.getProfileImageId(),
        myProfileDetailsProjection.getProfileImageUrl()
    );
  }
}
