CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_covering_batch_query
    ON users (status, modified_at) INCLUDE (user_id);
