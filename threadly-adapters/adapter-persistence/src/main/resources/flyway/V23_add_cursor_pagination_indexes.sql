--- 게시글 목록 조회 최적화
create index if not exists idx_posts_active_modified_id
    on posts(modified_at desc, post_id desc) where status = 'ACTIVE';

-- 게시글별 좋아요 수 조회 최적화
create index if not exists idx_post_likes_post_id
    on post_likes(post_id);

-- 특정 사용자의 좋아요 유무 확인 최적화
create index if not exists idx_post_likes_user_post
    on post_likes(user_id, post_id);

-- 게시글별 댓글 수 집계 최적화
create index if not exists idx_post_comments_active_post
    on post_comments(post_id) where status = 'ACTIVE';

-- 통계 업데이트
analyze
posts, post_likes, post_comments;