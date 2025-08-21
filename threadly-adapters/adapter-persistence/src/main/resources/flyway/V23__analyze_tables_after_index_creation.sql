-- Update query planner statistics after index creation in V23
-- Separated from V23 to avoid mixing DDL and maintenance commands

ANALYZE posts;
ANALYZE post_likes;  
ANALYZE post_comments;
ANALYZE comment_likes;
ANALYZE user_follows;
ANALYZE user_profile_images;
ANALYZE user_profile;