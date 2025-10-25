package com.threadly.batch.util;

import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.user.UserStatus;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배치 성능 테스트용 대용량 데이터 생성기
 * 실제 API 호출 대신 DB 직접 삽입 방식 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataGenerator {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 대용량 User 데이터 CSV 생성
     *
     * @param filePath CSV 파일 경로
     * @param count 생성할 데이터 개수
     * @param deletedRatio DELETED 상태 비율 (0.0 ~ 1.0)
     */
    public void generateUserCsv(String filePath, int count, double deletedRatio) throws IOException {
        log.info("Generating {} users to {}", count, filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // CSV 헤더
            writer.write("user_id,user_name,password,email,phone,user_type,status,is_email_verified,is_private,created_at,modified_at\n");
            
            for (int i = 1; i <= count; i++) {
                String userId = "perf-user-" + i;
                String userName = "PerfUser" + i;
                String email = "perfuser" + i + "@test.com";
                String phone = "010-" + String.format("%04d", random.nextInt(10000)) + "-" + String.format("%04d", random.nextInt(10000));
                
                // DELETED 상태 비율 적용
                UserStatus status = (random.nextDouble() < deletedRatio) ? UserStatus.DELETED : UserStatus.ACTIVE;
                
                // 삭제 대상은 과거 시간, 비대상은 최근 시간
                LocalDateTime modifiedAt = generateModifiedAt(status == UserStatus.DELETED);
                
                writer.write(String.format("%s,%s,password123,%s,%s,USER,%s,true,false,%s,%s\n",
                    userId, userName, email, phone, status.name(),
                    LocalDateTime.now().format(formatter),
                    modifiedAt.format(formatter)));
                
                if (i % 10000 == 0) {
                    log.info("Generated {} users", i);
                }
            }
        }
        
        log.info("User CSV generation completed: {}", filePath);
    }

    /**
     * 대용량 Post 데이터 CSV 생성
     */
    public void generatePostCsv(String filePath, int count, double deletedRatio) throws IOException {
        log.info("Generating {} posts to {}", count, filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("post_id,user_id,content,view_count,status,created_at,modified_at\n");
            
            for (int i = 1; i <= count; i++) {
                String postId = "perf-post-" + i;
                String userId = "perf-user-" + (random.nextInt(10000) + 1); // 기존 유저와 연결
                String content = "Performance test content for post " + i + " with some additional text to make it realistic.";
                int viewCount = random.nextInt(1000);
                
                PostStatus status = (random.nextDouble() < deletedRatio) ? PostStatus.DELETED : PostStatus.ACTIVE;
                LocalDateTime modifiedAt = generateModifiedAt(status == PostStatus.DELETED);
                
                writer.write(String.format("%s,%s,\"%s\",%d,%s,%s,%s\n",
                    postId, userId, content, viewCount, status.name(),
                    LocalDateTime.now().format(formatter),
                    modifiedAt.format(formatter)));
                
                if (i % 10000 == 0) {
                    log.info("Generated {} posts", i);
                }
            }
        }
        
        log.info("Post CSV generation completed: {}", filePath);
    }

    /**
     * 대용량 PostImage 데이터 CSV 생성
     */
    public void generatePostImageCsv(String filePath, int count, double deletedRatio) throws IOException {
        log.info("Generating {} post images to {}", count, filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("post_image_id,post_id,stored_file_name,image_order,image_url,status,created_at,modified_at\n");
            
            for (int i = 1; i <= count; i++) {
                String imageId = "perf-image-" + i;
                String postId = random.nextBoolean() ? "perf-post-" + (random.nextInt(50000) + 1) : null; // 일부는 orphan
                String storedFileName = "stored_" + UUID.randomUUID().toString() + ".jpg";
                String imageUrl = "https://test-bucket.s3.amazonaws.com/" + storedFileName;
                int imageOrder = random.nextInt(5) + 1;
                
                String status = (random.nextDouble() < deletedRatio) ? "DELETED" : "TEMPORARY";
                LocalDateTime modifiedAt = generateModifiedAt("DELETED".equals(status));
                
                writer.write(String.format("%s,%s,%s,%d,%s,%s,%s,%s\n",
                    imageId, postId, storedFileName, imageOrder, imageUrl, status,
                    LocalDateTime.now().format(formatter),
                    modifiedAt.format(formatter)));
                
                if (i % 10000 == 0) {
                    log.info("Generated {} post images", i);
                }
            }
        }
        
        log.info("PostImage CSV generation completed: {}", filePath);
    }

    /**
     * 삭제 대상 여부에 따른 수정 시간 생성
     */
    private LocalDateTime generateModifiedAt(boolean shouldBeDeleted) {
        LocalDateTime now = LocalDateTime.now();
        
        if (shouldBeDeleted) {
            // 삭제 대상: 30일 ~ 365일 전
            int daysAgo = random.nextInt(335) + 30;
            return now.minusDays(daysAgo).minusHours(random.nextInt(24));
        } else {
            // 비삭제 대상: 최근 7일 내
            int daysAgo = random.nextInt(7);
            return now.minusDays(daysAgo).minusHours(random.nextInt(24));
        }
    }

    /**
     * 모든 테스트 데이터 생성 (CSV)
     */
    public void generateAllTestData(String baseDir, int userCount, int postCount, int imageCount) throws IOException {
        generateUserCsv(baseDir + "/users.csv", userCount, 0.3); // 30% 삭제 대상
        generatePostCsv(baseDir + "/posts.csv", postCount, 0.2); // 20% 삭제 대상  
        generatePostImageCsv(baseDir + "/post_images.csv", imageCount, 0.4); // 40% 삭제 대상
        
        log.info("All test data generation completed in: {}", baseDir);
    }

    // ========================= DB 직접 삽입 메서드들 =========================

    /**
     * 대용량 User 데이터 DB 직접 삽입
     * 
     * @param count 생성할 데이터 개수
     * @param deletedRatio DELETED 상태 비율 (0.0 ~ 1.0)
     * @param batchSize 배치 처리 크기 (기본: 5000)
     */
    @Transactional
    public void generateUsersDirectToDB(int count, double deletedRatio, int batchSize) {
        log.info("Starting direct DB insert for {} users with batch size {}", count, batchSize);
        
        // 기존 테스트 데이터 정리
        cleanupExistingTestData();
        
        long startTime = System.currentTimeMillis();
        
        String sql = """
            INSERT INTO users (user_id, user_name, password, email, phone, user_type, status, 
                               is_email_verified, is_private, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<UserTestData> userBatch = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            UserTestData user = createUserTestData(i, deletedRatio);
            userBatch.add(user);
            
            // 배치 크기에 도달하면 DB에 삽입
            if (userBatch.size() >= batchSize) {
                insertUserBatch(sql, userBatch);
                userBatch.clear();
                
                if (i % (batchSize * 10) == 0) {
                    log.info("Inserted {} users", i);
                }
            }
        }
        
        // 남은 데이터 삽입
        if (!userBatch.isEmpty()) {
            insertUserBatch(sql, userBatch);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("User direct DB insert completed: {} users in {}ms", count, (endTime - startTime));
    }

    /**
     * 대용량 Post 데이터 DB 직접 삽입
     */
    @Transactional
    public void generatePostsDirectToDB(int count, double deletedRatio, int batchSize) {
        log.info("Starting direct DB insert for {} posts with batch size {}", count, batchSize);
        
        long startTime = System.currentTimeMillis();
        
        String sql = """
            INSERT INTO posts (post_id, user_id, content, view_count, status, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        List<PostTestData> postBatch = new ArrayList<>();
        
        // 먼저 존재하는 사용자 ID들을 가져와서 참조 무결성 유지
        List<String> availableUserIds = jdbcTemplate.queryForList(
            "SELECT user_id FROM users WHERE user_id LIKE 'perf-user-%' LIMIT 10000", String.class);
        
        if (availableUserIds.isEmpty()) {
            log.warn("No test users found. Please generate users first.");
            return;
        }
        
        for (int i = 1; i <= count; i++) {
            String randomUserId = availableUserIds.get(random.nextInt(availableUserIds.size()));
            PostTestData post = createPostTestData(i, randomUserId, deletedRatio);
            postBatch.add(post);
            
            if (postBatch.size() >= batchSize) {
                insertPostBatch(sql, postBatch);
                postBatch.clear();
                
                if (i % (batchSize * 10) == 0) {
                    log.info("Inserted {} posts", i);
                }
            }
        }
        
        if (!postBatch.isEmpty()) {
            insertPostBatch(sql, postBatch);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("Post direct DB insert completed: {} posts in {}ms", count, (endTime - startTime));
    }

    /**
     * 대용량 PostImage 데이터 DB 직접 삽입
     */
    @Transactional
    public void generatePostImagesDirectToDB(int count, double deletedRatio, int batchSize) {
        log.info("Starting direct DB insert for {} post images with batch size {}", count, batchSize);
        
        long startTime = System.currentTimeMillis();
        
        String sql = """
            INSERT INTO post_images (post_image_id, post_id, stored_file_name, image_order, 
                                     image_url, status, created_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<PostImageTestData> imageBatch = new ArrayList<>();
        
        // 존재하는 게시글 ID들 가져오기 (일부는 null로 orphan 이미지 생성)
        List<String> availablePostIds = jdbcTemplate.queryForList(
            "SELECT post_id FROM posts WHERE post_id LIKE 'perf-post-%' LIMIT 10000", String.class);
        
        for (int i = 1; i <= count; i++) {
            String randomPostId = null;
            if (!availablePostIds.isEmpty() && random.nextBoolean()) { // 50% 확률로 post와 연결
                randomPostId = availablePostIds.get(random.nextInt(availablePostIds.size()));
            }
            
            PostImageTestData image = createPostImageTestData(i, randomPostId, deletedRatio);
            imageBatch.add(image);
            
            if (imageBatch.size() >= batchSize) {
                insertPostImageBatch(sql, imageBatch);
                imageBatch.clear();
                
                if (i % (batchSize * 10) == 0) {
                    log.info("Inserted {} post images", i);
                }
            }
        }
        
        if (!imageBatch.isEmpty()) {
            insertPostImageBatch(sql, imageBatch);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("PostImage direct DB insert completed: {} images in {}ms", count, (endTime - startTime));
    }

    /**
     * 모든 성능 테스트 데이터를 DB에 직접 삽입
     */
    @Transactional
    public void generateAllTestDataDirectToDB(int userCount, int postCount, int imageCount, int batchSize) {
        log.info("Starting full performance test data generation - Users: {}, Posts: {}, Images: {}", 
                 userCount, postCount, imageCount);
        
        long totalStartTime = System.currentTimeMillis();
        
        // 1. 사용자 생성
        generateUsersDirectToDB(userCount, 0.3, batchSize);
        
        // 2. 게시글 생성
        generatePostsDirectToDB(postCount, 0.2, batchSize);
        
        // 3. 이미지 생성
        generatePostImagesDirectToDB(imageCount, 0.4, batchSize);
        
        long totalEndTime = System.currentTimeMillis();
        long totalDuration = totalEndTime - totalStartTime;
        
        log.info("=== Performance Test Data Generation Completed ===");
        log.info("Total Users: {}, Posts: {}, Images: {}", userCount, postCount, imageCount);
        log.info("Total Time: {}ms ({}s)", totalDuration, totalDuration / 1000.0);
        log.info("Batch Size: {}", batchSize);
    }

    // ========================= Helper 메서드들 =========================
    
    private UserTestData createUserTestData(int index, double deletedRatio) {
        String userId = "perf-user-" + index;
        String userName = "PerfUser" + index;
        String email = "perfuser" + index + "@test.com";
        String phone = "010-" + String.format("%04d", random.nextInt(10000)) + "-" + String.format("%04d", random.nextInt(10000));
        
        UserStatus status = (random.nextDouble() < deletedRatio) ? UserStatus.DELETED : UserStatus.ACTIVE;
        LocalDateTime modifiedAt = generateModifiedAt(status == UserStatus.DELETED);
        
        return new UserTestData(userId, userName, "password123", email, phone, 
                               "USER", status.name(), true, false, LocalDateTime.now(), modifiedAt);
    }
    
    private PostTestData createPostTestData(int index, String userId, double deletedRatio) {
        String postId = "perf-post-" + index;
        String content = "Performance test content for post " + index + 
                        " with some additional realistic text content to simulate real posts.";
        int viewCount = random.nextInt(1000);
        
        PostStatus status = (random.nextDouble() < deletedRatio) ? PostStatus.DELETED : PostStatus.ACTIVE;
        LocalDateTime modifiedAt = generateModifiedAt(status == PostStatus.DELETED);
        
        return new PostTestData(postId, userId, content, viewCount, status.name(), LocalDateTime.now(), modifiedAt);
    }
    
    private PostImageTestData createPostImageTestData(int index, String postId, double deletedRatio) {
        String imageId = "perf-image-" + index;
        String storedFileName = "stored_" + UUID.randomUUID().toString() + ".jpg";
        String imageUrl = "https://test-bucket.s3.amazonaws.com/" + storedFileName;
        int imageOrder = random.nextInt(5) + 1;
        
        String status = (random.nextDouble() < deletedRatio) ? "DELETED" : "TEMPORARY";
        LocalDateTime modifiedAt = generateModifiedAt("DELETED".equals(status));
        
        return new PostImageTestData(imageId, postId, storedFileName, imageOrder, 
                                   imageUrl, status, LocalDateTime.now(), modifiedAt);
    }

    private void insertUserBatch(String sql, List<UserTestData> userBatch) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserTestData user = userBatch.get(i);
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
            public int getBatchSize() {
                return userBatch.size();
            }
        });
    }

    private void insertPostBatch(String sql, List<PostTestData> postBatch) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostTestData post = postBatch.get(i);
                ps.setString(1, post.postId);
                ps.setString(2, post.userId);
                ps.setString(3, post.content);
                ps.setInt(4, post.viewCount);
                ps.setString(5, post.status);
                ps.setTimestamp(6, Timestamp.valueOf(post.createdAt));
                ps.setTimestamp(7, Timestamp.valueOf(post.modifiedAt));
            }

            @Override
            public int getBatchSize() {
                return postBatch.size();
            }
        });
    }

    private void insertPostImageBatch(String sql, List<PostImageTestData> imageBatch) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostImageTestData image = imageBatch.get(i);
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
            public int getBatchSize() {
                return imageBatch.size();
            }
        });
    }

    /**
     * 기존 테스트 데이터 정리
     */
    private void cleanupExistingTestData() {
        log.info("Cleaning up existing test data...");
        
        jdbcTemplate.update("DELETE FROM post_images WHERE post_image_id LIKE 'perf-image-%'");
        jdbcTemplate.update("DELETE FROM posts WHERE post_id LIKE 'perf-post-%'");
        jdbcTemplate.update("DELETE FROM users WHERE user_id LIKE 'perf-user-%'");
        
        log.info("Cleanup completed");
    }

    // ========================= 데이터 클래스들 =========================
    
    private static class UserTestData {
        final String userId, userName, password, email, phone, userType, status;
        final boolean isEmailVerified, isPrivate;
        final LocalDateTime createdAt, modifiedAt;
        
        UserTestData(String userId, String userName, String password, String email, String phone, 
                    String userType, String status, boolean isEmailVerified, boolean isPrivate,
                    LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.userId = userId;
            this.userName = userName;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.userType = userType;
            this.status = status;
            this.isEmailVerified = isEmailVerified;
            this.isPrivate = isPrivate;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }
    }
    
    private static class PostTestData {
        final String postId, userId, content, status;
        final int viewCount;
        final LocalDateTime createdAt, modifiedAt;
        
        PostTestData(String postId, String userId, String content, int viewCount, String status,
                    LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.postId = postId;
            this.userId = userId;
            this.content = content;
            this.viewCount = viewCount;
            this.status = status;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }
    }
    
    private static class PostImageTestData {
        final String postImageId, postId, storedFileName, imageUrl, status;
        final int imageOrder;
        final LocalDateTime createdAt, modifiedAt;
        
        PostImageTestData(String postImageId, String postId, String storedFileName, int imageOrder,
                         String imageUrl, String status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.postImageId = postImageId;
            this.postId = postId;
            this.storedFileName = storedFileName;
            this.imageOrder = imageOrder;
            this.imageUrl = imageUrl;
            this.status = status;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }
    }
}