package com.threadly.repository.user;

import com.threadly.entity.user.UserProfileEntity;
import com.threadly.user.profile.fetch.UserPreviewProjection;
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
//
//  /**
//   * userId에 해당하는 데이터가 존재하는지 조회
//   *
//   * @param userId
//   * @return
//   */
//  @Query(value = """
//      select exists(select 1 from user_profile where user_id = :userId)
//      """, nativeQuery = true)
//  boolean existsByUserId(String userId);

}
