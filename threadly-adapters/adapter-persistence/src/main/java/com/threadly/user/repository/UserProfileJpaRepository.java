package com.threadly.user.repository;

import com.threadly.user.entity.UserProfileEntity;
import com.threadly.user.profile.fetch.MyProfileDetailsProjection;
import com.threadly.user.profile.fetch.UserPreviewProjection;
import com.threadly.user.profile.fetch.UserProfileProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
             upi.image_url        as profileImageUrl
      from user_profile up
      left join user_profile_images upi on up.user_id = upi.user_id and upi.status = 'CONFIRMED'
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
      select up.user_id        as userId,
             up.nickname       as nickname,
             up.status_message as statusMessage,
             up.bio            as bio,
             u.phone           as phone,
             upi.image_url     as profileImageurl,
             u.status          as userStatus,
             u.is_private      as isPrivate
      from user_profile up
               left join users u on up.user_id = u.user_id
               left join user_profile_images upi on up.user_id = upi.user_id and upi.status = 'CONFIRMED'
      where u.user_id = :userId 
      """, nativeQuery = true)
  Optional<UserProfileProjection> findUserProfileByUserId(@Param("userId") String userId);

  /**
   * 내 프로필 정보 상세 조회
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select up.nickname               as nickname,
             up.status_message         as statusMessage,
             up.bio                    as bio,
             u.phone                   as phone,
             u.status                  as status,
             upi.user_profile_image_id as profileImageId,
             upi.image_url             as profileImageUrl
      from user_profile up
               join users u on up.user_id = u.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               where up.user_id = :userId;
      """, nativeQuery = true)
  Optional<MyProfileDetailsProjection> findMyProfileDetailsByUserId(@Param("userId") String userId);

  /**
   * 주어진 파라미터로 user profile 업데이트
   *
   * @param nickname
   * @param statusMessage
   * @param bio
   */
  @Modifying
  @Query("""
      update UserProfileEntity up
      set up.nickname = :nickname, up.statusMessage = :statusMessage, up.bio = :bio
      where up.userId = :userId
      """)
  void updateMyProfile(@Param("userId") String userId, @Param("nickname") String nickname,
      @Param("statusMessage") String statusMessage, @Param("bio") String bio);


}
