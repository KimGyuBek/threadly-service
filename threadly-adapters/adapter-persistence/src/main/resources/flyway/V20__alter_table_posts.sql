ALTER TABLE posts DROP CONSTRAINT posts_user_id_fkey;

ALTER TABLE posts
    ADD CONSTRAINT posts_user_id_fkey
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE;