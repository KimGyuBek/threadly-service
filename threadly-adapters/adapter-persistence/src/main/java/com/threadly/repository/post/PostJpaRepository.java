package com.threadly.repository.post;

import com.threadly.entity.post.PostEntity;
import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.PostStatusType;
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
             coalesce(pl.like_count, 0)    as likeCount,
             coalesce(pc.comment_count, 0) as commentCount,
             coalesce(pl.is_liked, false)  as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_profile_id = up.user_profile_id
               left join(select post_id,
                                count(*) as like_count,
                                max(
                                        case
                                            when user_id = :userId
                                                then true
                                            else false
                                            end
                                )        as is_liked
                         from post_likes
                         where post_id = :postId
                         group by post_id) pl on p.post_id = pl.post_id
               left join (select post_id, count(*) as comment_count
                          from post_comments
                          where post_id = :postId
                          group by post_id) pc on p.post_id = pc.post_id
      where p.post_id = :postId
      """, nativeQuery = true)
  Optional<PostDetailResponse> getPostDetailsByPostIdAndUserId(@Param("postId") String postId,
      @Param("userId") String userId);


  /**
   * 사용자에게 보이는 게시글 리스트 조회
   *
   * @param userId
   * @return
   */
  @Query(value = """
      select p.post_id                     as postId,
             u.user_id                     as userId,
             up.profile_image_url          as userProfileImageUrl,
             up.nickname                   as userNickname,
             p.content                     as content,
             p.view_count                  as viewCount,
             p.modified_at                 as postedAt,
             coalesce(pl.like_count, 0)    as likeCount,
             coalesce(pc.comment_count, 0) as commentCount,
             coalesce(pl.is_liked, false)  as liked
      from posts p
               join users u on p.user_id = u.user_id
               join user_profile up on u.user_profile_id = up.user_profile_id
               left join(select post_id,
                                count(*) as like_count,
                                max(
                                        case
                                            when user_id = :userId
                                                then true
                                            else false
                                            end
                                )        as is_liked
                         from post_likes
                         group by post_id) pl on p.post_id = pl.post_id
               left join(select post_id,
                                count(*) as comment_count
                         from post_comments
                         group by post_id) pc on p.post_id = pc.post_id
      where p.status = 'ACTIVE'
      order by p.modified_at desc
      """, nativeQuery = true)
  List<PostDetailResponse> getUserVisiblePostListByUserId(@Param("userId") String userId);

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
  void updateStatus(@Param("postId") String postId, @Param("status") PostStatusType status);

  @Query(value = """
      select 
      p.status
      from PostEntity p 
      where p.postId = :postId
      """)
  Optional<PostStatusType> findPostStatusByPostId(@Param(("postId")) String postId);


}
