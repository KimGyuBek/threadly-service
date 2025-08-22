package com.threadly.batch.util;

import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.user.UserStatusType;
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

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 배치 성능 테스트 데이터 삽입 시작 ===");
        
        // 기존 데이터 정리
        cleanupTestData();
        
        log.info("설정된 데이터 생성 규모 - 사용자: {}, 게시글: {}, 이미지: {}", userCount, postCount, imageCount);
        
        insertUsers(userCount);
        insertPosts(postCount);  
        insertPostImages(imageCount);
        
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

    // ========================= 데이터 생성 메서드들 =========================
    
    private UserData createUserData(int index) {
        String userId = "perf-user-" + index;
        String userName = "PerfUser" + index;
        String email = "perfuser" + index + "@test.com";
        String phone = "010-" + String.format("%04d", random.nextInt(10000)) + 
                      "-" + String.format("%04d", random.nextInt(10000));
        
        // 모든 사용자를 DELETED 상태로 생성 (배치 Job 실행 조건에 맞게)
        UserStatusType status = UserStatusType.DELETED;
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

    // ========================= 유틸리티 메서드들 =========================
    
    private void cleanupTestData() {
        log.info("기존 테스트 데이터 정리 중...");
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
}