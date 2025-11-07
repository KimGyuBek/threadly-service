package com.threadly.adapter.persistence.post.repository;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import com.threadly.core.port.post.out.projection.PostEngagementProjection;
import com.threadly.core.port.post.out.sesarch.PostSearchProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PostEntity JpaRepository
 */
public interface PostJpaRepository extends JpaRepository<PostEntity, String> {

  /**
   * postId로 content 업데이트
   *
   * @param postId
   * @param content
   */
  @Modifying
  @Query(value =
      "update PostEntity p "
          + "set p.content = :content "
          + "where p.postId = :postId"
  )
  void updatePostContentByPostId(@Param("postId") String postId,
      @Param("content") String content);

  /**
   * postId, userId로 게시글 상세 정보 조회
   *
   * @param postId
   * @param userId
   * @return
   */
  @Query(value = """
      select p.post_id                     as postId,
             u.user_id                     as userId,
             up.nickname                   as userNickname,
             p.content                     as content,
             p.view_count                  as viewCount,
             p.modified_at                 as postedAt,
             p.status                      as postStatus,
             upi.image_url as userProfileImageUrl,
             coalesce(pl.like_count, 0)    as likeCount,
             coalesce(pc.comment_count, 0) as commentCount,
             coalesce(pl.is_liked, false)  as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               left join(select post_id,
                                count(*) as like_count,
                                max(
                                        case
                                            when user_id = :userId
                                                then 1
                                            else 0
                                            end
                                ) > 0    as is_liked
                         from post_likes
                         where post_id = :postId
                         group by post_id) pl on p.post_id = pl.post_id
               left join (select post_id, count(*) as comment_count
                          from post_comments
                          where post_id = :postId
                          group by post_id) pc on p.post_id = pc.post_id
      where p.post_id = :postId
      """, nativeQuery = true)
  Optional<PostDetailProjection> getPostDetailsByPostIdAndUserId(@Param("postId") String postId,
      @Param("userId") String userId);


  /**
   * 사용자에게 노출되는 게시글 목록을 커서 기반으로 조회
   * <p>
   * 최신 게시글 부터 수정일(modified_at) 기준으로 내림차순 정렬되며, 커서 값보다 이전에 수정된 게시글들을 조회
   *
   * @param userId
   * @param cursor
   * @param cursorPostId
   * @param limit
   * @return
   */
  @Query(value = """
      select p.post_id                           as postId,
             u.user_id                           as userId,
             upi.image_url                       as userProfileImageUrl,
             up.nickname                         as userNickname,
             p.content                           as content,
             p.view_count                        as viewCount,
             p.modified_at                       as postedAt,
                   upi.image_url as userProfileImageUrl,
             coalesce(pl_count.like_count, 0)    as likeCount,
             coalesce(pc_count.comment_count, 0) as commentCount,
             coalesce(pl_liked.liked, false)     as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               left join(select post_id, count(*) as like_count
                         from post_likes
                         group by post_id) pl_count on p.post_id = pl_count.post_id
               left join(select post_id, count(*) as comment_count
                         from post_comments
                         group by post_id) pc_count on p.post_id = pc_count.post_id
               left join(select post_id,
                                true as liked
                         from post_likes
                         where user_id = :userId) pl_liked on p.post_id = pl_liked.post_id
      where p.status = 'ACTIVE'
        and (
              cast(:cursorPostedAt as timestamp) is null
              or
          p.modified_at < :cursorPostedAt
              or (
              p.modified_at = :cursorPostedAt and p.post_id < :cursorPostId
              )
          )
      order by p.modified_at DESC, p.post_id desc
      limit :limit     """, nativeQuery = true)
  List<PostDetailProjection> findUserVisiblePostsBeforeModifiedAt(@Param("userId") String userId,
      @Param("cursorPostedAt") LocalDateTime cursorPostedAt,
      @Param("cursorPostId") String cursorPostId,
      @Param("limit") int limit);

  /**
   * 게시글 상태 변경
   *
   * @param postId
   * @param status
   */
  @Modifying
  @Query("""
      update PostEntity p
      set p.status = :status
      where p.postId = :postId
      """)
  void updateStatus(@Param("postId") String postId, @Param("status") PostStatus status);

  @Query(value = """
      select 
      p.status
      from PostEntity p 
      where p.postId = :postId
      """)
  Optional<PostStatus> findPostStatusByPostId(@Param(("postId")) String postId);

  /**
   * 게시글 좋아요 정보 조회
   *
   * @param postId
   * @param userId
   * @return
   */
  @Query(value = """
      select p.post_id                    as postId,
             u.user_id                    as authorId,
             up.nickname                  as authorNickname,
             upi.image_url   as authorProfileImageUrl,
             p.content                    as content,
             coalesce(pl.like_count, 0)   as likeCount,
             coalesce(pl.is_liked, false) as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on up.user_id = upi.user_id
               left join(select post_id,
                                count(*) as like_count,
                                max(case
                                        when user_id = :userId
                                            then 1
                                        else 0
                                    end) > 0 as is_liked
                         from post_likes
                         where post_id = :postId
                         group by post_id) pl on p.post_id = pl.post_id
      where p.post_id = :postId and p.status = 'ACTIVE'
      """, nativeQuery = true)
  Optional<PostEngagementProjection> findPostEngagementByPostIdAndUserId(
      @Param("postId") String postId, @Param("userId") String userId);

  @Modifying()
  @Query("""
      update PostEntity p
      set p.viewCount = p.viewCount + 1
      where p.postId = :postId
      """)
  void increaseViewCount(@Param("postId") String postId);

  /**
   * postId로 작성자 userId 조회
   *
   * @param postId
   * @return
   */
  @Query(value = """
      select p.user_id
           from posts p
           where p.post_id = :postId and p.status = 'ACTIVE';
      """, nativeQuery = true)
  Optional<String> findUserIdByPostId(@Param("postId") String postId);

  /**
   * 주어진 userId에 해당하는 사용자가 게시글 검색 시 보여지는 데이터 조회
   *
   * @return
   */
  @Query(value = """
      select p.post_id                           as postId,
             p.content                           as content,
             coalesce(pl_count.like_count, 0)    as likeCount,
             coalesce(pc_count.comment_count, 0) as commentCount,
             coalesce(pl_liked.liked, false)     as liked,
             p.view_count                        as viewCount,
             p.user_id                           as userId,
             p.created_at                        as postedAt,
             up.nickname                         as userNickname,
             upi.image_url                       as userProfileImageUrl
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join user_profile_images upi on u.user_id = upi.user_id and upi.status = 'CONFIRMED'
               left join (select post_id, count(*) as like_count
                          from post_likes
                          group by post_id) pl_count on p.post_id = pl_count.post_id
               left join(select post_id, count(*) as comment_count
                         from post_comments
                         where status = 'ACTIVE'
                         group by post_id) pc_count on p.post_id = pc_count.post_id
               left join(select post_id, true as liked
                         from post_likes
                         where user_id = :userId) pl_liked on p.post_id = pl_liked.post_id
      where p.status = 'ACTIVE'
        and (
          u.is_private = false
              or p.user_id = :userId
              or exists(select 1
                        from user_follows uf
                        where uf.follower_id = :userId
                          and uf.following_id = p.user_id
                          and uf.status = 'APPROVED')
          )
            and p.content like concat('%', :keyword, '%')
        and (
          cast(:cursorPostedAt as timestamp) is null
              or p.created_at < :cursorPostedAt
              or (p.created_at = :cursorPostedAt and p.post_id < :cursorPostId)
          )
      order by case when :sortType = 'RECENT' then p.created_at end desc,
               case when :sortType = 'POPULAR' then coalesce(pl_count.like_count, 0) end desc,
               p.post_id desc
      limit :limit;
      """, nativeQuery = true)
  List<PostSearchProjection> searchVisiblePostsByKeywordWithCursor(
      @Param("userId") String userId,
      @Param("keyword") String keyword,
      @Param("cursorPostedAt") LocalDateTime cursorPostedAt,
      @Param("cursorPostId") String cursorPostId,
      @Param("limit") int limit,
      @Param("sortType") String sortType);

  /**
   * 주어진 targetUserId에 해당하는 사용자의 게시글 목록 커서 기반 조회
   *
   * @param requestUserId 요청자 ID (좋아요 여부 확인용)
   * @param targetUserId 대상 사용자 ID (게시글 필터링용)
   * @param cursorPostedAt
   * @param cursorPostId
   * @param limit
   * @return
   */
  @Query(value = """
      select p.post_id                         as postId,
             p.user_id                         as userId,
             up.nickname                       as userNickname,
             upi.image_url                     as userProfileImageUrl,
             p.content                         as content,
             p.view_count                      as viewCount,
             p.created_at                      as postedAt,
             p.status                          as postStatus,
             coalesce(pl_count.cnt, 0)         as likeCount,
             coalesce(pc_count.cnt, 0)         as commentCount,
             coalesce(user_liked.liked, false) as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_id = up.user_id
               left join (select upi.user_id, upi.image_url
                          from user_profile_images upi
                          where upi.status = 'APPROVED') upi
                         on u.user_id = upi.user_id
               left join(select pc.post_id, count(*) as cnt
                         from post_comments pc
                         where pc.status = 'ACTIVE'
                         group by pc.post_id) pc_count on p.post_id = pc_count.post_id
               left join (select pl.post_id, count(*) as cnt
                          from post_likes pl
                          group by pl.post_id) pl_count
                         on p.post_id = pl_count.post_id
               left join (select pl.post_id, true as liked
                          from post_likes pl
                          where pl.user_id = :requestUserId
                          group by pl.post_id) user_liked on p.post_id = user_liked.post_id
      where p.user_id = :targetUserId
        and p.status = 'ACTIVE'
        and (
          cast(:cursorPostedAt as timestamp) is null
              or p.created_at < :cursorPostedAt
              or (
              p.created_at = :cursorPostedAt and p.post_id < :cursorPostId)
          )
      order by p.created_at desc, p.post_id desc
      limit :limit
      """, nativeQuery = true)
  List<PostDetailProjection> getUserPostsByUserIdWithCursor(
      @Param("requestUserId") String requestUserId,
      @Param("targetUserId") String targetUserId,
      @Param("cursorPostedAt") LocalDateTime cursorPostedAt,
      @Param("cursorPostId") String cursorPostId, @Param("limit") int limit);
}
