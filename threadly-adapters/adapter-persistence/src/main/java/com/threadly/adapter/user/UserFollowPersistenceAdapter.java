package com.threadly.adapter.user;

import com.threadly.mapper.user.UserFollowMapper;
import com.threadly.repository.user.UserFollowJpaRepository;
import com.threadly.user.Follow;
import com.threadly.user.FollowStatusType;
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
public class UserFollowPersistenceAdapter implements FollowCommandPort, FollowQueryPort {

  private final UserFollowJpaRepository userFollowJpaRepository;

  @Override
  public void createFollow(Follow follow) {
    userFollowJpaRepository.save(UserFollowMapper.toEntity(follow));
  }

  @Override
  public void approveFollow(String followId) {

  }

  @Override
  public void rejectFollow(String followId) {

  }

  @Override
  public boolean isFollowing(String followerId, String followingId) {
    return userFollowJpaRepository.isFollowing(followerId, followingId);
  }

  @Override
  public Optional<FollowStatusType> findFollowStatusType(String followerId, String followingId) {
    return userFollowJpaRepository.findFollowStatusType(followerId, followingId);
  }

  @Override
  public List<FollowRequestsProjection> findFollowRequestsByCursor(String userId,
      LocalDateTime cursorFollowRequestedAt, String cursorFollowId, int limit) {
    return userFollowJpaRepository.findFollowRequestsByCursor(
        userId,
        cursorFollowRequestedAt,
        cursorFollowId,
        limit
    );
  }

  @Override
  public List<FollowerProjection> findFollowersByCursor(String targetUserId,
      LocalDateTime cursorFollowedAt, String cursorFollowerId, int limit) {
    return userFollowJpaRepository.findFollowersByCursor(targetUserId, cursorFollowedAt,
        cursorFollowerId, limit);
  }

  @Override
  public List<FollowingProjection> findFollowingsByCursor(String targetUserId,
      LocalDateTime cursorFollowedAt, String cursorFollowingId, int limit) {
    return userFollowJpaRepository.findFollowingsByCursor(targetUserId, cursorFollowedAt,
        cursorFollowingId, limit);
  }
}
