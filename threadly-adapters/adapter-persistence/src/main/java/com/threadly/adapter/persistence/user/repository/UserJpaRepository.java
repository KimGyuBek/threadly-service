package com.threadly.adapter.persistence.user.repository;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.user.out.search.UserSearchProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByEmail(String email);

  /**
   * isEmailVerified 변경
   *
   * @param userId
   * @param isEmailVerified
   */
  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.isEmailVerified = :isEmailVerified "
          + "where u.userId = :userId")
  void updateEmailVerification(@Param("userId") String userId,
      @Param("isEmailVerified") boolean isEmailVerified);

  /**
   * 주어진 email에 해당하는 사용자 존재 유무
   *
   * @param email
   * @return
   */
  boolean existsByEmail(String email);

  /**
   * status 변경
   *
   * @param userId
   * @param statusType
   */
  @Modifying
  @Query(
      value = "update UserEntity u "
          + "set u.userStatus = :statusType "
          + "where u.userId = :userId")
  void updateStatus(@Param("userId") String userId,
      @Param("statusType") UserStatus statusType);


  /**
   * 주어진 userId에 해당하는 사용자의 phone 업데이트
   *
   * @param userId
   * @param phone
   */
  @Modifying
  @Query("""
      update UserEntity  u
      set u.phone = :phone
      where u.userId = :userId
      """)
  void updatePhoneByUserId(@Param("userId") String userId, @Param("phone") String phone);

  /**
   * 주어진 userId에 해당하는 사용자의 password 변경
   *
   * @param userId
   * @param newPassword
   */
  @Modifying
  @Query("""
      update UserEntity u
      set u.password = :newPassword
      where u.userId = :userId
      """)
  void updatePasswordByUserId(@Param("userId") String userId,
      @Param("newPassword") String newPassword);

  /**
   * 주어진 userId에 해당하는 사용자의 is_private 변경
   *
   * @param userId
   * @param isPrivate
   */
  @Modifying
  @Query("""
      update UserEntity u
       set u.isPrivate = :isPrivate 
       where u.userId = :userId
      """)
  void updatePrivacyByUserId(@Param("userId") String userId, @Param("isPrivate") boolean isPrivate);

  /**
   * 주어진 userId에 해당하는 사용자의 isPrivate 조회
   *
   * @param userId
   * @return
   */
  @Query("""
      select u.isPrivate from UserEntity  u where u.userId = :userId
      """)
  boolean isUserPrivate(@Param("userId") String userId);

  /**
   * 주어진 userId에 해당하는 사용자의 userStatus 조회
   *
   * @param userId
   * @return
   */
  @Query("""
      select u.userStatus from UserEntity u where u.userId = :userId
      """)
  Optional<UserStatus> getUserStatusType(@Param("userId") String userId);

  /**
   * 주어진 파라미터에 해당하는 nickname 검색
   *
   * @return
   */
  @Query(value = """
      with page as (select u.user_id,
                           up.nickname
                    from users u
                             join user_profile up on u.user_id = up.user_id
                    where u.status = 'ACTIVE'
                      and (:keyword is null or up.nickname ilike concat(:keyword, '%'))
                      and (:cursorNickname is null or up.nickname < :cursorNickname)
                    order by up.nickname desc
                    limit :limit)
      select p.user_id                   as userId,
             p.nickname                  as userNickname,
             upi.image_url               as userProfileImageUrl,
             coalesce(uf.status, 'NONE') as followStatus
      from page p
               left join user_profile_images upi on p.user_id = upi.user_id and upi.status = 'CONFIRMED'
               left join user_follows uf on p.user_id = uf.following_id and uf.follower_id = :userId
      order by p.nickname desc;
      """, nativeQuery = true)
  List<UserSearchProjection> searchUserByKeywordWithCursor(
      @Param("userId") String userId,
      @Param("keyword") String keyword,
      @Param("cursorNickname") String cursorNickname,
      @Param("limit") int limit
  );
}
