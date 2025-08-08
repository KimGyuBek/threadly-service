package com.threadly.follow.adapter;

import com.threadly.follow.Follow;
import com.threadly.follow.FollowStatusType;
import com.threadly.follow.mapper.FollowMapper;
import com.threadly.follow.repository.FollowJpaRepository;
import com.threadly.user.follow.FollowCommandPort;
import com.threadly.user.follow.FollowQueryPort;
import com.threadly.user.follow.FollowRequestsProjection;
import com.threadly.user.follow.FollowerProjection;
import com.threadly.user.follow.FollowingProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 사용자 팔로우 Persistence Adapter
 */
@Repository
@RequiredArgsConstructor
public class FollowPersistenceAdapter implements FollowCommandPort, FollowQueryPort {

  private final FollowJpaRepository followJpaRepository;

  @Override
  public void createFollow(Follow follow) {
    followJpaRepository.save(FollowMapper.toEntity(follow));
  }

  @Override
  public void updateFollowStatus(Follow follow) {
    followJpaRepository.updateFollowStatusById(follow.getFollowId(), follow.getStatusType());
  }

  @Override
  public void deleteFollow(String followId) {
    followJpaRepository.deleteById(followId);
  }

  @Override
  public boolean isFollowing(String followerId, String followingId) {
    return followJpaRepository.isFollowing(followerId, followingId);
  }

  @Override
  public Optional<FollowStatusType> findFollowStatusType(String followerId, String followingId) {
    return followJpaRepository.findFollowStatusType(followerId, followingId);
  }

  @Override
  public List<FollowRequestsProjection> findFollowRequestsByCursor(String userId,
      LocalDateTime cursorTimestamp, String cursorId, int limit) {
    return followJpaRepository.findFollowRequestsByCursor(
        userId,
        cursorTimestamp,
        cursorId,
        limit
    );
  }

  @Override
  public List<FollowerProjection> findFollowersByCursor(String targetUserId,
      LocalDateTime cursorTimestamp, String cursorId, int limit) {
    return followJpaRepository.findFollowersByCursor(targetUserId, cursorTimestamp,
        cursorId, limit);
  }

  @Override
  public List<FollowingProjection> findFollowingsByCursor(String targetUserId,
      LocalDateTime cursorTimestamp, String cursorId, int limit) {
    return followJpaRepository.findFollowingsByCursor(targetUserId, cursorTimestamp,
        cursorId, limit);
  }

  @Override
  public Optional<Follow> findByIdAndStatusType(String followId,
      FollowStatusType followStatusType) {
    return followJpaRepository.findByFollowIdAndStatusType(followId, followStatusType)
        .map(FollowMapper::toDomain);
  }

  @Override
  public void deleteByFollowerIdAndFollowingIdAndStatusType(String followerId,
      String followingId, FollowStatusType statusType) {

    followJpaRepository.deleteByFollowerIdAndFollowingIdAndStatusType(
        followerId, followingId, statusType
    );
  }

  @Override
  public boolean existsByFollowerIdAndFollowingIdAndStatusType(String followerId,
      String followingId, FollowStatusType statusType) {
    return followJpaRepository.existsByFollowerIdAndFollowingIdAndStatusType(
        followerId, followingId, statusType
    );
  }
}
