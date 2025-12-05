package com.threadly.adapter.persistence.follow.repository;

import com.threadly.adapter.persistence.follow.entity.FollowEntity;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.follow.out.projection.FollowRequestsProjection;
import com.threadly.core.port.follow.out.projection.FollowerProjection;
import com.threadly.core.port.follow.out.projection.FollowingProjection;
import com.threadly.core.port.follow.out.projection.UserFollowStatsProjection;
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
  Optional<FollowStatus> findFollowStatusType(@Param("followerId") String followerId,
      @Param("followingId") String followingId);

  /**
   * 팔로우 요청 목록 커서 기반 조회
   *
   * @param userId
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @return
   */
  @Query(value = """
      with page as (select uf.follow_id,
                           uf.follower_id,
                           uf.created_at
                    from user_follows uf
                    where uf.following_id = :userId
                      and uf.status = 'PENDING'
                      and (
                        cast(:cursorTimestamp as timestamp) is null
                            or uf.created_at < :cursorTimestamp
                            or (uf.created_at = :cursorTimestamp and uf.follow_id < :cursorId)
                        )
                    order by uf.created_at desc, uf.follow_id desc
                    limit :limit)
      select p.follow_id   as followId,
             p.follower_id as requesterId,
             up.nickname   as requesterNickname,
             upi.image_url as requesterProfileImageUrl,
             p.created_at  as followRequestedAt
      from page p
               join user_profile up on up.user_id = p.follower_id
               left join user_profile_images upi on upi.user_id = p.follower_id and upi.status = 'CONFIRMED'
      order by p.created_at desc, p.follow_id desc;
      """, nativeQuery = true)
  List<FollowRequestsProjection> findFollowRequestsByCursor(@Param("userId") String userId,
      @Param("cursorTimestamp") LocalDateTime cursorTimestamp,
      @Param("cursorId") String cursorId, @Param("limit") int limit);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로워 목록 커서 기반 조회
   *
   * @param targetUserId
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @return
   */
  @Query(value = """
      with page as (select uf.follower_id,
                           uf.modified_at
                    from user_follows uf
                             join users target_user
                                  on target_user.user_id = uf.following_id and target_user.status = 'ACTIVE'
                             join users follower_user
                                  on follower_user.user_id = uf.follower_id and follower_user.status = 'ACTIVE'
                    where uf.following_id = :targetUserId
                      and uf.status = 'APPROVED'
                      and (
                        cast(:cursorTimestamp as timestamp) is null
                            or uf.modified_at < :cursorTimestamp
                            or (uf.modified_at = :cursorTimestamp and uf.follower_id < :cursorId)
                        )
                    order by uf.modified_at desc, uf.follower_id desc
                    limit :limit)
      select p.follower_id as followerId,
             up.nickname   as followerNickname,
             upi.image_url as followerProfileImageUrl,
             p.modified_at as followedAt
      from page p
               join user_profile up on up.user_id = p.follower_id
               left join user_profile_images upi on upi.user_id = p.follower_id and upi.status = 'CONFIRMED'
      order by p.modified_at desc, p.follower_id desc;
      """, nativeQuery = true)
  List<FollowerProjection> findFollowersByCursor(@Param("targetUserId") String targetUserId,
      @Param("cursorTimestamp") LocalDateTime cursorTimestamp,
      @Param("cursorId") String cursorId, @Param("limit") int limit);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 팔로잉 목록 커서 기반 조회
   *
   * @param targetUserId
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @return
   */
  @Query(value = """
      with page as (select uf.following_id,
                           uf.modified_at
                    from user_follows uf
                             join users target_user
                                  on target_user.user_id = uf.follower_id and target_user.status = 'ACTIVE'
                             join users following_user
                                  on following_user.user_id = uf.following_id and following_user.status = 'ACTIVE'
                    where uf.follower_id = :targetUserId
                      and uf.status = 'APPROVED'
                      and (
                        cast(:cursorTimestamp as timestamp) is null
                            or uf.modified_at < :cursorTimestamp
                            or (uf.modified_at = :cursorTimestamp and uf.following_id < :cursorId)
                        )
                    order by uf.modified_at desc, uf.following_id desc
                    limit :limit)
      select p.following_id as followingId,
             up.nickname    as followingNickname,
             upi.image_url  as followingProfileImageUrl,
             p.modified_at  as followedAt
      from page p
               join user_profile up on up.user_id = p.following_id
               left join user_profile_images upi on upi.user_id = p.following_id and upi.status = 'CONFIRMED'
      order by p.modified_at desc, p.following_id desc;
      """, nativeQuery = true)
  List<FollowingProjection> findFollowingsByCursor(@Param("targetUserId") String targetUserId,
      @Param("cursorTimestamp") LocalDateTime cursorTimestamp,
      @Param("cursorId") String cursorId, @Param("limit") int limit);

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
      @Param("statusType") FollowStatus statusType);

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
      @Param("statusType") FollowStatus statusType);

  /**
   * 주어진 파라미터에 해당하는 팔로우 삭제
   *
   * @param followerId
   * @param followingId
   * @param statusType
   */
  @Modifying
  @Query("""
      delete from FollowEntity f
      where f.follower.userId = :followerId 
        and f.following.userId = :followingId 
          and f.statusType = :statusType
      """)
  void deleteByFollowerIdAndFollowingIdAndStatusType(@Param("followerId") String followerId,
      @Param("followingId") String followingId,
      @Param("statusType") FollowStatus statusType);

  /**
   * 주어진 파라미터에 해당하는 팔로우 존재 유무 조회
   *
   * @param followerId
   * @param followingId
   * @param statusType
   * @return
   */
  @Query("""
      select exists (
        select f
         from FollowEntity f
         where f.follower.userId = :followerId 
          and f.following.userId = :followingId 
            and f.statusType = :statusType)
      """)
  boolean existsByFollowerIdAndFollowingIdAndStatusType(@Param("followerId") String followerId,
      @Param("followingId") String followingId, @Param("statusType") FollowStatus statusType);

  /**
   * 주어진 userId에 해당하는 사용자의 팔로워, 팔로잉 수 조회
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select coalesce(sum(case
                              when uf.following_id = :userId and uf.status = 'APPROVED' and
                                   follower.status = 'ACTIVE' then 1
                              else 0 end), 0) as followerCount,
             coalesce(sum(case
                              when uf.follower_id = :userId and uf.status = 'APPROVED' and
                                   following.status = 'ACTIVE' then 1
                              else 0 end), 0) as followingCount
      from users u
               left join user_follows uf on (u.user_id = uf.following_id or u.user_id = uf.follower_id)
               left join users follower on (uf.follower_id = follower.user_id)
               left join users following on (uf.following_id = following.user_id)
      where u.user_id = :userId
        and u.status = 'ACTIVE';
      """, nativeQuery = true)
  UserFollowStatsProjection getUserFollowStatsByUserId(@Param("userId") String userId);
}
