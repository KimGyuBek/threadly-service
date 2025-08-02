package com.threadly.adapter.user;

import com.threadly.mapper.user.UserFollowMapper;
import com.threadly.repository.user.UserFollowJpaRepository;
import com.threadly.user.Follow;
import com.threadly.user.FollowStatusType;
import com.threadly.user.follow.FollowCommandPort;
import com.threadly.user.follow.FollowQueryPort;
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
}
