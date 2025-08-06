package com.threadly.user.repository;

import com.threadly.user.entity.UserProfileImageEntity;
import com.threadly.image.ImageStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * UserProfileImage Jpa Repository
 */
public interface UserProfileImageJpaRepository extends
    JpaRepository<UserProfileImageEntity, String> {


  /**
   * 주어진 userId를 가진 사용자 프로필 이미지 중, 현재 상태가 CONFIRMED인 경우에만 ststus를 변경한다.
   *
   * @param userId
   * @param status
   */
  @Modifying
  @Query("""
      update UserProfileImageEntity
      set status = :status
      where userProfileImageId = :imageId 
      """)
  void updateStatusById(@Param("imageId") String profileImageId,
      @Param("status") ImageStatus status);

  /**
   * 주어진 userProfileImageId에 해당하는 이미지의 status와 userId를 주어진 값으로 변경
   *
   * @param userProfileImageId
   * @param status
   */
  @Modifying
  @Query("""
      update UserProfileImageEntity
      set status = :status , userProfile.userId = :userId
      where userProfileImageId = :userProfileImageId
      """)
  void updateStatusAndUserIdByUserProfileImageId(
      @Param("userProfileImageId") String userProfileImageId,
      @Param("userId") String userId,
      @Param("status") ImageStatus status);

  /**
   * 주어진 userProfileImageId에 해당하며 이미지 존재 유무 검증
   *
   * @param userProfileImageId
   * @return
   */
  @Query("""
       select exists (
       select upi
       from UserProfileImageEntity upi
       where upi.userProfileImageId = :userProfileImageId and upi.status != 'DELETED' )
      """)
  boolean existsNotDeletedByUserProfileImageId(
      @Param("userProfileImageId") String userProfileImageId);

  /**
   * 주어진 userId에 해당하고 'CONFIRMED' 상태의 이미지 id 조회
   *
   * @param userId
   * @return
   */
  @Query("""
      select upi.userProfileImageId
      from UserProfileImageEntity upi
      where upi.userProfile.userId = :userId and upi.status = 'CONFIRMED'
      """)
  Optional<String> findConfirmedProfileImageIdByUserId(@Param("userId") String userId);
}
