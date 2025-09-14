package com.threadly.core.service.user.profile;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.follow.FollowStatusType;
import com.threadly.core.domain.user.UserStatusType;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.user.in.profile.query.UserProfileQueryUseCase;
import com.threadly.core.port.user.in.profile.query.dto.GetMyProfileDetailsApiResponse;
import com.threadly.core.port.user.in.profile.query.dto.GetUserProfileApiResponse;
import com.threadly.core.port.user.out.profile.query.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.query.MyProfileDetailsProjection;
import com.threadly.core.port.user.out.profile.query.UserProfileProjection;
import com.threadly.core.service.validator.follow.FollowAccessValidator;
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
public class UserProfileQueryQueryService implements UserProfileQueryUseCase {

  private final UserProfileQueryPort userProfileQueryPort;

  private final FollowAccessValidator followAccessValidator;

  @Override
  public boolean existsUserProfile(String userId) {
    return userProfileQueryPort.existsUserProfileByUserId(userId);
  }

  @Override
  public void validateNicknameUnique(String nickname) {
    if (userProfileQueryPort.existsByNickname(nickname)) {
      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }
  }

  @Transactional(readOnly = true)
  @Override
  public GetUserProfileApiResponse getUserProfile(String userId, String targetUserId) {
    UserProfileProjection userProfileProjection = userProfileQueryPort.findUserProfileByUserId(
        targetUserId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    /*사용자 상태 검증*/
    if (userProfileProjection.getUserStatus().equals(UserStatusType.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    } else if (userProfileProjection.getUserStatus().equals(UserStatusType.INACTIVE)) {
      throw new UserException(ErrorCode.USER_INACTIVE);
    }

    /*팔로우 유무 검증*/
    FollowStatusType followStatusType = followAccessValidator.validateProfileAccessible(userId,
        targetUserId);

    return new GetUserProfileApiResponse(
        new UserPreview(
            userProfileProjection.getUserId(),
            userProfileProjection.getNickname(),
            userProfileProjection.getProfileImageUrl()
        ),
        userProfileProjection.getStatusMessage(),
        userProfileProjection.getBio(),
        followStatusType
    );
  }


  @Override
  public GetMyProfileDetailsApiResponse getMyProfileDetails(String userId) {
    /*사용자 상태 검증*/
    MyProfileDetailsProjection myProfileDetailsProjection = userProfileQueryPort.findMyProfileDetailsByUserId(
        userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*사용자 상태 검증*/
    if (myProfileDetailsProjection.getStatus().equals(UserStatusType.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    }

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
