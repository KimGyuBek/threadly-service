package com.threadly.post.repository;

import com.threadly.post.entity.PostLikeEntity;
import com.threadly.post.like.post.PostLikerProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostLikeEntity Jpa Repository
 */
public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, String> {

  /**
   * userId, postId에 일치하는 데이터가 있는지 조회
   *
   * @param postId
   * @param userId
   * @return
   */
  @Query("""
      select count(p) > 0 
      from PostLikeEntity p 
      where p.id.postId=:postId
      and p.id.userId=:userId
      """)
  boolean existByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);

  /**
   * postId로 데이터 수 조회
   *
   * @param postId
   * @return
   */
  @Query("""
      select count(p)
      from PostLikeEntity p
      where p.id.postId=:postId
      """)
  long countByPostId(@Param("postId") String postId);

  /**
   * postId, userId와 일치하는 데이터 삭제
   *
   * @param postId
   * @param userId
   * @return
   */
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = """
      delete from PostLikeEntity p
      where p.id.postId=:postId
      and p.id.userId=:userId
      """)
  int deleteByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);

  /**
   * 특정 게시글에 좋아요를 누른 사람 목록을 커서 기반으로 조회
   * <p>
   * 가장 최근 좋아요 부터 생성일(created_at)을 기준으로 내림차순 정렬되며, 커서 값 보다 이전에 생성된 좋아요를 누른 사용자들을 조회
   *
   * @param postId
   * @param cursorLikedAt
   * @param cursorLikerId
   * @param limit
   * @return
   */
  @Query(value = """
      select pl.user_id           as likerId,
             up.nickname          as likerNickname,
             upi.image_url        as likerProfileImageUrl,
             up.bio               as likerBio,
             pl.created_at        as likedAt
      from post_likes pl
               join users u on pl.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
      where pl.post_id = :postId
       and (:cursorLikedAt is null 
       or pl.created_at < :cursorLikedAt 
       or (pl.created_at = :cursorLikedAt and pl.user_id < :cursorLikerId))
      order by pl.created_at desc, pl.user_id desc
      limit :limit;
      
      """, nativeQuery = true)
  List<PostLikerProjection> getPostLikersBeforeCreatedAt(@Param("postId") String postId,
      @Param("cursorLikedAt")
      LocalDateTime cursorLikedAt, @Param("cursorLikerId") String cursorLikerId,
      @Param("limit") int limit);

  /**
   * 특정 게시글의 좋아요 전체 삭제
   *
   * @param postId
   */
  @Modifying
  @Query("""
      delete from PostLikeEntity pl
      where pl.id.postId=:postId
      """)
  void deleteAllByPostId(@Param("postId") String postId);

  /**
   * postId에 해당하는 좋아요 수 조회
   *
   * @param postId
   */
  @Query(value = """
      select count(p) from PostLikeEntity p where p.id.postId=:postId
      """)
  int findAllByPostId(@Param("postId") String postId);
}
