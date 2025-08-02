package com.threadly.repository.user;

import com.threadly.entity.user.UserFollowEntity;
import com.threadly.user.FollowStatusType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * UserFollow Jpa Repository
 */
public interface UserFollowJpaRepository extends JpaRepository<UserFollowEntity, String> {

  /**
   * followerId 사용자가 followingId 사용자를 팔로우하고 있는지 여부 조회
   *
   * @param followerId
   * @param followingId
   * @return
   */
  @Query("""
      select exists (
      select 1 
      from UserFollowEntity 
      where follower.userId = :followerId 
      and following.userId = :followingId)
      """)
  boolean isFollowing(@Param("followerId") String followerId,
      @Param("followingId") String followingId);

  /**
   * followerId, followingId에 해당하는 FollowStatusType 조회
   *
   * @param followerId
   * @param followingId
   * @return
   */
  @Query("""
      select uf.statusType
      from UserFollowEntity uf 
      where uf.follower.userId = :followerId 
        and uf.following.userId = :followingId
      """)
  Optional<FollowStatusType> findFollowStatusType(@Param("followerId") String followerId,
      @Param("followingId") String followingId);
}
