-- 기존 FK 제거
ALTER TABLE post_images DROP CONSTRAINT post_images_post_id_fkey;

-- CASCADE 로 재생성
ALTER TABLE posts
    ADD CONSTRAINT post_images_post_id_fkey
        FOREIGN KEY (post_id)
            REFERENCES posts(post_id)
            ON DELETE CASCADE;