-- Add CASCADE DELETE to user foreign key references for performance optimization
-- This resolves the performance issue in userHardDeleteDeletedJob

-- Fix posts table user_id foreign key constraint
ALTER TABLE posts DROP CONSTRAINT IF EXISTS posts_user_id_fkey;
ALTER TABLE posts ADD CONSTRAINT posts_user_id_fkey 
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

-- Fix user_follows table follower_id foreign key constraint  
ALTER TABLE user_follows DROP CONSTRAINT IF EXISTS user_follows_follower_id_fkey;
ALTER TABLE user_follows ADD CONSTRAINT user_follows_follower_id_fkey 
    FOREIGN KEY (follower_id) REFERENCES users (user_id) ON DELETE CASCADE;
    
-- Fix user_follows table following_id foreign key constraint
ALTER TABLE user_follows DROP CONSTRAINT IF EXISTS user_follows_following_id_fkey;  
ALTER TABLE user_follows ADD CONSTRAINT user_follows_following_id_fkey
    FOREIGN KEY (following_id) REFERENCES users (user_id) ON DELETE CASCADE;

-- Fix user_profile_images table user_id foreign key constraint
ALTER TABLE user_profile_images DROP CONSTRAINT IF EXISTS user_profile_images_user_id_fkey;
ALTER TABLE user_profile_images ADD CONSTRAINT user_profile_images_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

-- Fix post_images table (check if it has user_id reference)
-- Note: post_images references posts.post_id, and posts now has CASCADE to users
-- So this will cascade automatically: users -> posts -> post_images