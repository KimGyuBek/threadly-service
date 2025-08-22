```sql
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

-- Additional compound index for user_follows unique constraint optimization
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_user_follows_follower_following
    ON user_follows (follower_id, following_id);

```

### 이거를 flyway로 실행하면 항상 멈춰. 근데 내가 콘솔에서 수동으로실행하면 성공하는데 뭐가 문제일까

---
### 원인

아하, 여기서 핵심은 CREATE INDEX CONCURRENTLY 때문이라 카네.

Postgres의 CONCURRENTLY 옵션은 인덱스 만드는 동안 테이블에 write lock을 안 걸려고 쓰는 옵션인데, 그 대신 몇 가지 제약이 있다:
Postgres의 CREATE INDEX CONCURRENTLY 는 transaction block 안에서 실행 불가다 (ERROR: CREATE INDEX CONCURRENTLY
cannot run inside a transaction block).
• Flyway는 executeInTransaction=false 옵션을 주면, 해당 SQL 마이그레이션은 autocommit 모드로 실행한다. 여기까진 맞아.
• 그런데, CONCURRENTLY는 단순히 “트랜잭션 바깥”에서만 실행되면 끝이 아니라, 두 번의 스캔을 수행하고 중간에 백그라운드로 진행되기 때문에, 커넥션/세션이 유지되면서
전체 과정을 마쳐야 한다.
• Flyway는 JDBC로 실행하면서 autocommit=true 세션을 열고 쿼리 던지는데, 드라이버/플랫폼 환경에 따라 중간 상태에서 세션을 끊어버리는 경우가 있어 멈춘
것처럼 보일 수 있다.

 트랜잭션 안에서는 실행 불가
   CREATE INDEX CONCURRENTLY 는 반드시 autocommit 모드에서 실행돼야 한다. 즉, BEGIN ... COMMIT 안에서 돌리면 에러 나거나 세션이
   걸려버린다.
   Flyway는 기본적으로 각 마이그레이션 스크립트를 하나의 트랜잭션으로 감싸서 실행하기 때문에, CONCURRENTLY가 안 먹고 멈추는 거다.
   (네가 콘솔에서 수동으로 실행하면 autocommit 모드라서 잘 되는 거고.)
---

### 해결책

• Flyway 마이그레이션 파일에 트랜잭션 끄기 옵션을 줘야 한다.
SQL 기반 마이그레이션이라면 파일 이름 앞에 V 대신 U(Repeatable / Undo 아님 주의) 쓰는 게 아니고, Flyway 9 이후부터는 flyway.conf나 SQL
script header로 -- flyway:executeInTransaction=false 를 지정할 수 있다.