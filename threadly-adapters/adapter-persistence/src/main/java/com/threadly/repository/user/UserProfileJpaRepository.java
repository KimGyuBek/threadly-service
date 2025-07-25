package com.threadly.repository.user;

import com.threadly.entity.user.UserProfileEntity;
import com.threadly.user.profile.fetch.UserPreviewProjection;
import com.threadly.user.profile.fetch.UserProfileProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * UserProfile Jpa Repository
 */
public interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, String> {

  /**
   * userId로 user comment preview 조회
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select up.nickname          as nickname,
             up.profile_image_url as profileImageUrl
      from user_profile up
      where up.user_id = :userId;
      """, nativeQuery = true)
  UserPreviewProjection findUserCommentPreviewByUserId(@Param("userId") String userId);

  /**
   * nickname 존재 여부 조회
   *
   * @param nickname
   * @return
   */
  @Query("""
      select exists(select up from UserProfileEntity up where up.nickname = :nickname)
      """)
  boolean existsByNickname(@Param("nickname") String nickname);

  /**
   * useId에 해당하는 userprofile 조회
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select
          up.user_id as userId,
          up.nickname as nickname,
          up.status_message as statusMessage,
          up.bio as bio,
          u.phone as phone,
          up.profile_image_url as profileImageUrl
      from user_profile up
      left join users u on u.user_id = up.user_id
      where up.user_id = :userId
      """, nativeQuery = true)
  Optional<UserProfileProjection> findUserProfileByUserId(@Param("userId") String userId);

}
