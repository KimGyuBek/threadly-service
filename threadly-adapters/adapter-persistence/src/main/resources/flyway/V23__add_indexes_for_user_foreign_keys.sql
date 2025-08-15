-- Add indexes for user foreign key references to optimize CASCADE DELETE performance
-- This addresses the performance issue in userHardDeleteDeletedJob where multiple
-- related tables need to be scanned for referential integrity checks

-- Index for posts.user_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_posts_user_id 
    ON posts (user_id);

-- Index for post_likes.user_id  
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_post_likes_user_id
    ON post_likes (user_id);

-- Index for post_comments.user_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_post_comments_user_id
    ON post_comments (user_id);

-- Index for comment_likes.user_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comment_likes_user_id
    ON comment_likes (user_id);

-- Index for user_follows.follower_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_user_follows_follower_id
    ON user_follows (follower_id);

-- Index for user_follows.following_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_user_follows_following_id
    ON user_follows (following_id);

-- Index for user_profile_images.user_id
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_user_profile_images_user_id
    ON user_profile_images (user_id);

-- Index for user_profile.user_id (already has PK but adding explicit index for clarity)
-- Note: Primary key already creates index, but documenting for completeness
-- user_profile.user_id is PRIMARY KEY so index already exists

-- Additional compound index for user_follows unique constraint optimization
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_user_follows_follower_following
    ON user_follows (follower_id, following_id);