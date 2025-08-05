package com.threadly.repository.follow;

import com.threadly.entity.follow.FollowEntity;
import com.threadly.follow.FollowStatusType;
import com.threadly.user.follow.FollowRequestsProjection;
import com.threadly.user.follow.FollowerProjection;
import com.threadly.user.follow.FollowingProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * UserFollow Jpa Repository
 */
public interface FollowJpaRepository extends JpaRepository<FollowEntity, String> {

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
      from FollowEntity 
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
      from FollowEntity uf 
      where uf.follower.userId = :followerId 
        and uf.following.userId = :followingId
      """)
  Optional<FollowStatusType> findFollowStatusType(@Param("followerId") String followerId,
      @Param("followingId") String followingId);

  /**
   * 팔로우 요청 목록 커서 기반 조회
   *
   * @param userId
   * @param cursorFollowRequestedAt
   * @param cursorFollowId
   * @param limit
   * @return
   */
  @Query(value = """
      select uf.follow_id   as followId,
             uf.follower_id as requesterId,
             up.nickname    as requesterNickname,
             upi.image_url  as requesterProfileImageUrl,
             uf.created_at  as followRequestedAt
      from user_follows uf
               join user_profile up on up.user_id = uf.follower_id
               left join user_profile_images upi
                         on upi.user_id = uf.follower_id and upi.status = 'CONFIRMED'
      where uf.following_id = :userId
        and uf.status = 'PENDING'
        and (:cursorFollowRequestedAt is null
          or uf.created_at < :cursorFollowRequestedAt
          or (uf.created_at = :cursorFollowRequestedAt and uf.follow_id < :cursorFollowId)
          )
      order by uf.created_at desc, uf.follow_id desc
      limit :limit;
      """, nativeQuery = true)
  List<FollowRequestsProjection> findFollowRequestsByCursor(@Param("userId") String userId,
      @Param("cursorFollowRequestedAt") LocalDateTime cursorFollowRequestedAt,
      @Param("cursorFollowId") String cursorFollowId, @Param("limit") int limit);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로워 목록 커서 기반 조회
   *
   * @param targetUserId
   * @param cursorFollowedAt
   * @param cursorFollowerId
   * @param limit
   * @return
   */
  @Query(value = """
      select uf.follower_id as followerId,
             up.nickname    as followerNickname,
             upi.image_url  as followerProfileImageUrl,
             uf.modified_at as followedAt
      from user_follows uf
               join users target_user
                    on target_user.user_id = uf.following_id and target_user.status = 'ACTIVE'
               join users u on u.user_id = uf.follower_id and u.status = 'ACTIVE'
               join user_profile up on up.user_id = uf.follower_id
               left join user_profile_images upi on upi.user_id = uf.follower_id
      where uf.following_id = :targetUserId and uf.status = 'APPROVED'
        and (:cursorFollowedAt is null
          or uf.modified_at < :cursorFollowedAt or
             (uf.modified_at = :cursorFollowedAt and uf.follower_id < :cursorFollowerId))
      order by uf.modified_at desc, uf.follower_id desc
      limit :limit
      """, nativeQuery = true)
  List<FollowerProjection> findFollowersByCursor(@Param("targetUserId") String targetUserId,
      @Param("cursorFollowedAt") LocalDateTime cursorFollowedAt,
      @Param("cursorFollowerId") String cursorFollowerId, @Param("limit") int limit);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로잉 목록 커서 기반 조회
   *
   * @param targetUserId
   * @param cursorFollowedAt
   * @param cursorFollowingId
   * @param limit
   * @return
   */
  @Query(value = """
      select uf.following_id as followingId,
             up.nickname    as followingNickname,
             upi.image_url  as followingProfileImageUrl,
             uf.modified_at as followedAt
      from user_follows uf
               join users target_user
                    on target_user.user_id = uf.follower_id and target_user.status = 'ACTIVE'
               join users u on u.user_id = uf.following_id and u.status = 'ACTIVE'
               join user_profile up on up.user_id = uf.following_id
               left join user_profile_images upi on upi.user_id = uf.following_id
      where uf.follower_id = :targetUserId and uf.status = 'APPROVED'
        and (:cursorFollowedAt is null
          or uf.modified_at < :cursorFollowedAt or
             (uf.modified_at = :cursorFollowedAt and uf.following_id < :cursorFollowingId))
      order by uf.modified_at desc, uf.following_id desc
      limit :limit
      """, nativeQuery = true)
  List<FollowingProjection> findFollowingsByCursor(@Param("targetUserId") String targetUserId,
      @Param("cursorFollowedAt") LocalDateTime cursorFollowedAt,
      @Param("cursorFollowingId") String cursorFollowingId, @Param("limit") int limit);

  /**
   * 주어진 followId에 해당하는 팔로우의 주어진 status로 변경
   *
   * @param followId
   * @param statusType
   */
  @Modifying
  @Query("""
      update FollowEntity set statusType = :statusType where followId = :followId
      """)
  void updateFollowStatusById(@Param("followId") String followId,
      @Param("statusType") FollowStatusType statusType);

  /**
   * 주어진 followId, followStatusType에 해당하는 팔로우 조회
   *
   * @param followId
   * @param statusType
   * @return
   */
  @Query("""
      select f from FollowEntity  f where f.followId = :followId and f.statusType = :statusType
      """)
  Optional<FollowEntity> findByFollowIdAndStatusType(@Param("followId") String followId,
      @Param("statusType") FollowStatusType statusType);
}
