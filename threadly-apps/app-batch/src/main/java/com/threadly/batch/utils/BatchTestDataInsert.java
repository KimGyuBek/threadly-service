package com.threadly.batch.utils;

import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.user.UserStatus;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 배치 성능 테스트용 데이터 삽입 
 * 실행: --spring.profiles.active=data-insert
 */
@Slf4j
@Component
@Profile("data-insert")
@RequiredArgsConstructor
public class BatchTestDataInsert implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();
    
    @Value("${data-insert.user-count}")
    private int userCount;
    
    @Value("${data-insert.post-count}")
    private int postCount;
    
    @Value("${data-insert.image-count}")
    private int imageCount;

    @Value("${data-insert.like-count:500000}")
    private int likeCount;

    @Value("${data-insert.comment-count:300000}")
    private int commentCount;

    @Value("${data-insert.comment-like-count:100000}")
    private int commentLikeCount;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 배치 성능 테스트 데이터 삽입 시작 ===");
        
        // 기존 데이터 정리
        cleanupTestData();
        
        log.info("설정된 데이터 생성 규모 - 사용자: {}, 게시글: {}, 이미지: {}, 좋아요: {}, 댓글: {}, 댓글 좋아요: {}",
                userCount, postCount, imageCount, likeCount, commentCount, commentLikeCount);

        insertUsers(userCount);
        insertPosts(postCount);
        insertPostImages(imageCount);
        insertPostLikes(likeCount);
        insertPostComments(commentCount);
        insertCommentLikes(commentLikeCount);

        log.info("=== 데이터 삽입 완료 ===");
        logDataStatus();
    }

    /**
     * 사용자 데이터 삽입
     */
    private void insertUsers(int count) {
        log.info("사용자 데이터 삽입 시작: {}개", count);
        
        long startTime = System.currentTimeMillis();
        
        String sql = """
            INSERT INTO users (user_id, user_name, password, email, phone, user_type, status, 
                               is_email_verified, is_private, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<UserData> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            users.add(createUserData(i));
            
            // 5000개씩 배치 처리
            if (users.size() == 5000) {
                insertUserBatch(sql, users);
                users.clear();
                log.info("사용자 {}개 삽입 완료", i);
            }
        }
        
        // 남은 데이터 처리
        if (!users.isEmpty()) {
            insertUserBatch(sql, users);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("사용자 삽입 완료: {}개, {}ms", count, duration);
    }

    /**
     * 게시글 데이터 삽입
     */
    private void insertPosts(int count) {
        log.info("게시글 데이터 삽입 시작: {}개", count);
        
        long startTime = System.currentTimeMillis();
        
        // 존재하는 사용자 ID 가져오기
        List<String> userIds = jdbcTemplate.queryForList(
            "SELECT user_id FROM users WHERE user_id LIKE 'perf-user-%' LIMIT 10000", String.class);
        
        if (userIds.isEmpty()) {
            log.error("사용자 데이터가 없습니다. 사용자를 먼저 생성하세요.");
            return;
        }
        
        String sql = """
            INSERT INTO posts (post_id, user_id, content, view_count, status, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        List<PostData> posts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String randomUserId = userIds.get(random.nextInt(userIds.size()));
            posts.add(createPostData(i, randomUserId));
            
            if (posts.size() == 5000) {
                insertPostBatch(sql, posts);
                posts.clear();
                log.info("게시글 {}개 삽입 완료", i);
            }
        }
        
        if (!posts.isEmpty()) {
            insertPostBatch(sql, posts);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("게시글 삽입 완료: {}개, {}ms", count, duration);
    }

    /**
     * 이미지 데이터 삽입
     */
    private void insertPostImages(int count) {
        log.info("이미지 데이터 삽입 시작: {}개", count);
        
        long startTime = System.currentTimeMillis();
        
        // 존재하는 게시글 ID 가져오기
        List<String> postIds = jdbcTemplate.queryForList(
            "SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%' LIMIT 10000", String.class);
        
        String sql = """
            INSERT INTO post_images (post_image_id, post_id, stored_file_name, image_order, 
                                     image_url, status, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<ImageData> images = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String randomPostId = null;
            if (!postIds.isEmpty() && random.nextBoolean()) {
                randomPostId = postIds.get(random.nextInt(postIds.size()));
            }
            images.add(createImageData(i, randomPostId));
            
            if (images.size() == 5000) {
                insertImageBatch(sql, images);
                images.clear();
                log.info("이미지 {}개 삽입 완료", i);
            }
        }
        
        if (!images.isEmpty()) {
            insertImageBatch(sql, images);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("이미지 삽입 완료: {}개, {}ms", count, duration);
    }

    /**
     * 게시글 좋아요 데이터 삽입 (파레토 법칙 적용)
     * 상위 20% 인기 게시글에 80% 좋아요 집중
     */
    private void insertPostLikes(int count) {
        log.info("게시글 좋아요 데이터 삽입 시작: {}개 (파레토 법칙 적용)", count);

        long startTime = System.currentTimeMillis();

        // 존재하는 게시글 ID 가져오기 (created_at DESC 순서)
        List<String> allPostIds = jdbcTemplate.queryForList(
            "SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%' ORDER BY created_at DESC",
            String.class);

        if (allPostIds.isEmpty()) {
            log.error("게시글 데이터가 없습니다. 게시글을 먼저 생성하세요.");
            return;
        }

        // 상위 20% 인기 게시글, 나머지 80% 일반 게시글
        int topPostCount = (int)(allPostIds.size() * 0.2);
        List<String> topPosts = allPostIds.subList(0, Math.min(topPostCount, allPostIds.size()));
        List<String> normalPosts = allPostIds.size() > topPostCount
            ? allPostIds.subList(topPostCount, allPostIds.size())
            : new ArrayList<>();

        // 사용자 ID 가져오기
        List<String> userIds = jdbcTemplate.queryForList(
            "SELECT user_id FROM users WHERE user_id LIKE 'perf-user-%' LIMIT 10000", String.class);

        if (userIds.isEmpty()) {
            log.error("사용자 데이터가 없습니다.");
            return;
        }

        String sql = """
            INSERT INTO post_likes (post_id, user_id, created_at)
            VALUES (?, ?, ?)
            ON CONFLICT (post_id, user_id) DO NOTHING
            """;

        // 80% 좋아요를 상위 20% 게시글에 할당
        int topPostLikesCount = (int)(count * 0.8);
        insertPostLikesForPosts(sql, topPosts, userIds, topPostLikesCount, "인기 게시글");

        // 20% 좋아요를 나머지 80% 게시글에 할당
        int normalPostLikesCount = count - topPostLikesCount;
        if (!normalPosts.isEmpty()) {
            insertPostLikesForPosts(sql, normalPosts, userIds, normalPostLikesCount, "일반 게시글");
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("게시글 좋아요 삽입 완료: 목표 {}개, {}ms", count, duration);
    }

    /**
     * 특정 게시글들에 좋아요 삽입
     */
    private void insertPostLikesForPosts(String sql, List<String> postIds, List<String> userIds,
                                          int targetCount, String postType) {
        log.info("  {} 좋아요 삽입 시작: 목표 {}개, 대상 게시글 {}개", postType, targetCount, postIds.size());

        List<PostLikeData> likes = new ArrayList<>();
        int insertedCount = 0;

        while (insertedCount < targetCount) {
            String randomPostId = postIds.get(random.nextInt(postIds.size()));
            String randomUserId = userIds.get(random.nextInt(userIds.size()));

            // 1년 이내 랜덤 시간
            LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(365));

            likes.add(new PostLikeData(randomPostId, randomUserId, createdAt));
            insertedCount++;

            if (likes.size() >= 5000) {
                insertPostLikeBatch(sql, likes);
                likes.clear();
                log.info("  {} 좋아요 {}개 삽입 진행 중...", postType, insertedCount);
            }
        }

        if (!likes.isEmpty()) {
            insertPostLikeBatch(sql, likes);
        }

        log.info("  {} 좋아요 삽입 완료: {}개", postType, targetCount);
    }

    /**
     * 댓글 데이터 삽입 (파레토 법칙 적용)
     */
    private void insertPostComments(int count) {
        log.info("댓글 데이터 삽입 시작: {}개 (파레토 법칙 적용)", count);

        long startTime = System.currentTimeMillis();

        // 존재하는 게시글 ID 가져오기
        List<String> allPostIds = jdbcTemplate.queryForList(
            "SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%' ORDER BY created_at DESC",
            String.class);

        if (allPostIds.isEmpty()) {
            log.error("게시글 데이터가 없습니다.");
            return;
        }

        // 상위 20% 인기 게시글, 나머지 80% 일반 게시글
        int topPostCount = (int)(allPostIds.size() * 0.2);
        List<String> topPosts = allPostIds.subList(0, Math.min(topPostCount, allPostIds.size()));
        List<String> normalPosts = allPostIds.size() > topPostCount
            ? allPostIds.subList(topPostCount, allPostIds.size())
            : new ArrayList<>();

        // 사용자 ID 가져오기
        List<String> userIds = jdbcTemplate.queryForList(
            "SELECT user_id FROM users WHERE user_id LIKE 'perf-user-%' LIMIT 10000", String.class);

        if (userIds.isEmpty()) {
            log.error("사용자 데이터가 없습니다.");
            return;
        }

        String sql = """
            INSERT INTO post_comments (comment_id, post_id, user_id, content, status, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        // 전역 인덱스 관리 (중복 방지)
        int currentIndex = 1;

        // 80% 댓글을 상위 20% 게시글에 할당
        int topPostCommentsCount = (int)(count * 0.8);
        insertCommentsForPosts(sql, topPosts, userIds, topPostCommentsCount, currentIndex, "인기 게시글");
        currentIndex += topPostCommentsCount;

        // 20% 댓글을 나머지 80% 게시글에 할당
        int normalPostCommentsCount = count - topPostCommentsCount;
        if (!normalPosts.isEmpty()) {
            insertCommentsForPosts(sql, normalPosts, userIds, normalPostCommentsCount, currentIndex, "일반 게시글");
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("댓글 삽입 완료: 목표 {}개, {}ms", count, duration);
    }

    /**
     * 특정 게시글들에 댓글 삽입
     */
    private void insertCommentsForPosts(String sql, List<String> postIds, List<String> userIds,
                                         int targetCount, int startIndex, String postType) {
        log.info("  {} 댓글 삽입 시작: 목표 {}개, 대상 게시글 {}개", postType, targetCount, postIds.size());

        List<CommentData> comments = new ArrayList<>();

        for (int i = 0; i < targetCount; i++) {
            String randomPostId = postIds.get(random.nextInt(postIds.size()));
            String randomUserId = userIds.get(random.nextInt(userIds.size()));

            int commentIndex = startIndex + i;
            comments.add(createCommentData(commentIndex, randomPostId, randomUserId));

            if (comments.size() >= 5000) {
                insertCommentBatch(sql, comments);
                comments.clear();
                log.info("  {} 댓글 {}개 삽입 진행 중...", postType, startIndex + i);
            }
        }

        if (!comments.isEmpty()) {
            insertCommentBatch(sql, comments);
        }

        log.info("  {} 댓글 삽입 완료: {}개", postType, targetCount);
    }

    /**
     * 댓글 좋아요 데이터 삽입
     */
    private void insertCommentLikes(int count) {
        log.info("댓글 좋아요 데이터 삽입 시작: {}개", count);

        long startTime = System.currentTimeMillis();

        // 존재하는 댓글 ID 가져오기 (랜덤하게 선택)
        List<String> commentIds = jdbcTemplate.queryForList(
            "SELECT comment_id FROM post_comments WHERE comment_id LIKE 'perf-comment-%' ORDER BY random() LIMIT 30000",
            String.class);

        if (commentIds.isEmpty()) {
            log.error("댓글 데이터가 없습니다. 댓글을 먼저 생성하세요.");
            return;
        }

        // 사용자 ID 가져오기
        List<String> userIds = jdbcTemplate.queryForList(
            "SELECT user_id FROM users WHERE user_id LIKE 'perf-user-%' LIMIT 10000", String.class);

        if (userIds.isEmpty()) {
            log.error("사용자 데이터가 없습니다.");
            return;
        }

        String sql = """
            INSERT INTO comment_likes (comment_id, user_id, created_at)
            VALUES (?, ?, ?)
            ON CONFLICT (comment_id, user_id) DO NOTHING
            """;

        List<CommentLikeData> likes = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String randomCommentId = commentIds.get(random.nextInt(commentIds.size()));
            String randomUserId = userIds.get(random.nextInt(userIds.size()));
            LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(365));

            likes.add(new CommentLikeData(randomCommentId, randomUserId, createdAt));

            if (likes.size() >= 5000) {
                insertCommentLikeBatch(sql, likes);
                likes.clear();
                log.info("댓글 좋아요 {}개 삽입 진행 중...", i);
            }
        }

        if (!likes.isEmpty()) {
            insertCommentLikeBatch(sql, likes);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("댓글 좋아요 삽입 완료: 목표 {}개, {}ms", count, duration);
    }

    // ========================= 데이터 생성 메서드들 =========================
    
    private UserData createUserData(int index) {
        String userId = "perf-user-" + index;
        String userName = "PerfUser" + index;
        String email = "perfuser" + index + "@test.com";
        String phone = "010-" + String.format("%04d", random.nextInt(10000)) + 
                      "-" + String.format("%04d", random.nextInt(10000));
        
        // 모든 사용자를 DELETED 상태로 생성 (배치 Job 실행 조건에 맞게)
        UserStatus status = UserStatus.DELETED;
        LocalDateTime now = LocalDateTime.now();
        
        return new UserData(userId, userName, "password123", email, phone, 
                           "USER", status.name(), true, false, now, now);
    }
    
    private PostData createPostData(int index, String userId) {
        String postId = "perf-post-" + index;
        String content = "Performance test content for post " + index + " with realistic text.";
        int viewCount = random.nextInt(1000);
        
        // 모든 게시글을 DELETED 상태로 생성 (배치 Job 실행 조건에 맞게)
        PostStatus status = PostStatus.DELETED;
        LocalDateTime now = LocalDateTime.now();
        
        return new PostData(postId, userId, content, viewCount, status.name(), 
                           now, now);
    }
    
    private ImageData createImageData(int index, String postId) {
        String imageId = "perf-image-" + index;
        String storedFileName = "stored_" + UUID.randomUUID().toString() + ".jpg";
        String imageUrl = "https://test-bucket.s3.amazonaws.com/" + storedFileName;
        int imageOrder = random.nextInt(5) + 1;

        // 이미지는 DELETED와 TEMPORARY 상태로 50:50 비율로 생성 (배치 Job 실행 조건에 맞게)
        String status = (random.nextBoolean()) ? "DELETED" : "TEMPORARY";
        LocalDateTime now = LocalDateTime.now();

        return new ImageData(imageId, postId, storedFileName, imageOrder,
                           imageUrl, status, now, now);
    }

    private CommentData createCommentData(int index, String postId, String userId) {
        String commentId = "perf-comment-" + index;
        String content = "테스트 댓글 " + index + " - " + UUID.randomUUID().toString().substring(0, 20);
        LocalDateTime now = LocalDateTime.now().minusDays(random.nextInt(365));

        return new CommentData(commentId, postId, userId, content, "ACTIVE", now, now);
    }


    // ========================= 배치 삽입 메서드들 =========================
    
    private void insertUserBatch(String sql, List<UserData> users) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserData user = users.get(i);
                ps.setString(1, user.userId);
                ps.setString(2, user.userName);
                ps.setString(3, user.password);
                ps.setString(4, user.email);
                ps.setString(5, user.phone);
                ps.setString(6, user.userType);
                ps.setString(7, user.status);
                ps.setBoolean(8, user.isEmailVerified);
                ps.setBoolean(9, user.isPrivate);
                ps.setTimestamp(10, Timestamp.valueOf(user.createdAt));
                ps.setTimestamp(11, Timestamp.valueOf(user.modifiedAt));
            }

            @Override
            public int getBatchSize() { return users.size(); }
        });
    }

    private void insertPostBatch(String sql, List<PostData> posts) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostData post = posts.get(i);
                ps.setString(1, post.postId);
                ps.setString(2, post.userId);
                ps.setString(3, post.content);
                ps.setInt(4, post.viewCount);
                ps.setString(5, post.status);
                ps.setTimestamp(6, Timestamp.valueOf(post.createdAt));
                ps.setTimestamp(7, Timestamp.valueOf(post.modifiedAt));
            }

            @Override
            public int getBatchSize() { return posts.size(); }
        });
    }

    private void insertImageBatch(String sql, List<ImageData> images) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ImageData image = images.get(i);
                ps.setString(1, image.postImageId);
                ps.setString(2, image.postId);
                ps.setString(3, image.storedFileName);
                ps.setInt(4, image.imageOrder);
                ps.setString(5, image.imageUrl);
                ps.setString(6, image.status);
                ps.setTimestamp(7, Timestamp.valueOf(image.createdAt));
                ps.setTimestamp(8, Timestamp.valueOf(image.modifiedAt));
            }

            @Override
            public int getBatchSize() { return images.size(); }
        });
    }

    private void insertPostLikeBatch(String sql, List<PostLikeData> likes) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostLikeData like = likes.get(i);
                ps.setString(1, like.postId);
                ps.setString(2, like.userId);
                ps.setTimestamp(3, Timestamp.valueOf(like.createdAt));
            }

            @Override
            public int getBatchSize() { return likes.size(); }
        });
    }

    private void insertCommentBatch(String sql, List<CommentData> comments) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CommentData comment = comments.get(i);
                ps.setString(1, comment.commentId);
                ps.setString(2, comment.postId);
                ps.setString(3, comment.userId);
                ps.setString(4, comment.content);
                ps.setString(5, comment.status);
                ps.setTimestamp(6, Timestamp.valueOf(comment.createdAt));
                ps.setTimestamp(7, Timestamp.valueOf(comment.modifiedAt));
            }

            @Override
            public int getBatchSize() { return comments.size(); }
        });
    }

    private void insertCommentLikeBatch(String sql, List<CommentLikeData> likes) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CommentLikeData like = likes.get(i);
                ps.setString(1, like.commentId);
                ps.setString(2, like.userId);
                ps.setTimestamp(3, Timestamp.valueOf(like.createdAt));
            }

            @Override
            public int getBatchSize() { return likes.size(); }
        });
    }

    // ========================= 유틸리티 메서드들 =========================
    
    private void cleanupTestData() {
        log.info("기존 테스트 데이터 정리 중...");
        jdbcTemplate.update("DELETE FROM comment_likes WHERE comment_id IN (SELECT comment_id FROM post_comments WHERE comment_id LIKE 'perf-comment-%')");
        jdbcTemplate.update("DELETE FROM post_comments WHERE comment_id LIKE 'perf-comment-%'");
        jdbcTemplate.update("DELETE FROM post_likes WHERE post_id IN (SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%')");
        jdbcTemplate.update("DELETE FROM post_images WHERE post_image_id LIKE 'perf-image-%'");
        jdbcTemplate.update("DELETE FROM posts WHERE post_id LIKE 'perf-post-%'");
        jdbcTemplate.update("DELETE FROM users WHERE user_id LIKE 'perf-user-%'");
        log.info("기존 데이터 정리 완료");
    }

    private void logDataStatus() {
        int userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE user_id LIKE 'perf-user-%'", Integer.class);
        int postCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM posts WHERE post_id LIKE 'perf-post-%'", Integer.class);
        int imageCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM post_images WHERE post_image_id LIKE 'perf-image-%'", Integer.class);
        int postLikeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM post_likes WHERE post_id IN (SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%')", Integer.class);
        int commentCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM post_comments WHERE comment_id LIKE 'perf-comment-%'", Integer.class);
        int commentLikeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM comment_likes WHERE comment_id IN (SELECT comment_id FROM post_comments WHERE comment_id LIKE 'perf-comment-%')", Integer.class);

        int deletedUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE user_id LIKE 'perf-user-%' AND status = 'DELETED'", Integer.class);
        int deletedPosts = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM posts WHERE post_id LIKE 'perf-post-%' AND status = 'DELETED'", Integer.class);
        int deletedImages = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM post_images WHERE post_image_id LIKE 'perf-image-%' AND status = 'DELETED'", Integer.class);

        log.info("=== 데이터 현황 ===");
        log.info("사용자: {}개 (삭제 대상: {}개)", userCount, deletedUsers);
        log.info("게시글: {}개 (삭제 대상: {}개)", postCount, deletedPosts);
        log.info("이미지: {}개 (삭제 대상: {}개)", imageCount, deletedImages);
        log.info("게시글 좋아요: {}개", postLikeCount);
        log.info("댓글: {}개", commentCount);
        log.info("댓글 좋아요: {}개", commentLikeCount);
    }

    // ========================= 데이터 클래스들 =========================
    
    private static class UserData {
        final String userId, userName, password, email, phone, userType, status;
        final boolean isEmailVerified, isPrivate;
        final LocalDateTime createdAt, modifiedAt;
        
        UserData(String userId, String userName, String password, String email, String phone,
                String userType, String status, boolean isEmailVerified, boolean isPrivate,
                LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.userId = userId; this.userName = userName; this.password = password;
            this.email = email; this.phone = phone; this.userType = userType; 
            this.status = status; this.isEmailVerified = isEmailVerified; 
            this.isPrivate = isPrivate; this.createdAt = createdAt; this.modifiedAt = modifiedAt;
        }
    }
    
    private static class PostData {
        final String postId, userId, content, status;
        final int viewCount;
        final LocalDateTime createdAt, modifiedAt;
        
        PostData(String postId, String userId, String content, int viewCount, String status,
                LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.postId = postId; this.userId = userId; this.content = content;
            this.viewCount = viewCount; this.status = status; 
            this.createdAt = createdAt; this.modifiedAt = modifiedAt;
        }
    }
    
    private static class ImageData {
        final String postImageId, postId, storedFileName, imageUrl, status;
        final int imageOrder;
        final LocalDateTime createdAt, modifiedAt;

        ImageData(String postImageId, String postId, String storedFileName, int imageOrder,
                 String imageUrl, String status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.postImageId = postImageId; this.postId = postId; this.storedFileName = storedFileName;
            this.imageOrder = imageOrder; this.imageUrl = imageUrl; this.status = status;
            this.createdAt = createdAt; this.modifiedAt = modifiedAt;
        }
    }

    private static class PostLikeData {
        final String postId, userId;
        final LocalDateTime createdAt;

        PostLikeData(String postId, String userId, LocalDateTime createdAt) {
            this.postId = postId;
            this.userId = userId;
            this.createdAt = createdAt;
        }
    }

    private static class CommentData {
        final String commentId, postId, userId, content, status;
        final LocalDateTime createdAt, modifiedAt;

        CommentData(String commentId, String postId, String userId, String content, String status,
                   LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.commentId = commentId;
            this.postId = postId;
            this.userId = userId;
            this.content = content;
            this.status = status;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }
    }

    private static class CommentLikeData {
        final String commentId, userId;
        final LocalDateTime createdAt;

        CommentLikeData(String commentId, String userId, LocalDateTime createdAt) {
            this.commentId = commentId;
            this.userId = userId;
            this.createdAt = createdAt;
        }
    }
}