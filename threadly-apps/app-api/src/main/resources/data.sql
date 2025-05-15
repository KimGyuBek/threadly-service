-- 사용자 1
-- 프로필 등록
insert into user_profile(user_profile_id,
                         nickname,
                         status_message,
                         bio,
                         gender,
                         profile_type,
                         profile_image_url)
values ('up1',
        'nickname',
        'status message',
        'bio',
        'MALE',
        'USER',
        '/');

-- email 인증 사용자
insert into users (user_id,
                   user_name,
                   password,
                   email,
                   phone,
                   user_type,
                   is_active,
                   is_email_verified,
                   user_profile_id,
                   created_at,
                   modified_at)
values ('usr1',
        'user_email_verified1',
        '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'user_email_verified1@test.com',
        '123-1234-1234',
        'USER',
        true,
        true,
        'up1',
        '2025-04-22 00:55:59.733403',
        '2025-04-22 00:55:59.733403');

-- 사용자 2
-- 프로필 등록
insert into user_profile(user_profile_id,
                         nickname,
                         status_message,
                         bio,
                         gender,
                         profile_type,
                         profile_image_url)
values ('up2',
        'nickname2',
        'status message2',
        'bio2',
        'MALE',
        'USER',
        '/');

-- email 인증 사용자
insert into users (user_id,
                   user_name,
                   password,
                   email,
                   phone,
                   user_type,
                   is_active,
                   is_email_verified,
                   user_profile_id,
                   created_at,
                   modified_at)
values ('usr2',
        'user_email_verified2',
        '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'user_email_verified2@test.com',
        '123-1234-1234',
        'USER',
        true,
        true,
        'up2',
        '2025-04-22 00:55:59.733403',
        '2025-04-22 00:55:59.733403');


--사용자 3
-- email 미인증 사용자
insert into users (user_id,
                   user_name,
                   password,
                   email,
                   phone,
                   user_type,
                   is_active,
                   is_email_verified,
                   created_at,
                   modified_at)
values ('usr3',
        'user_email_not_verified',
        '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'user_email_not_verified@test.com',
        '123-1234-1234',
        'USER',
        true,
        false,
        '2025-04-22 00:55:59.733403',
        '2025-04-22 00:55:59.733403');

--- 게시글
-- 사용자 1이 작성한 게시글 5개
insert into posts (post_id, user_id, content, view_count, created_at, modified_at)
values ('post1', 'usr1', '카페에서 커피 마시다 옆 테이블 대화에 집중한 지 30분째. 이제 걔네보다 내가 더 잘 앎.', 42,
        current_timestamp, current_timestamp),
       ('post2', 'usr1', '방 청소한다고 음악 틀었는데 결국 춤만 추다 끝남. 방은 그대로다.', 37, current_timestamp,
        current_timestamp),
       ('post3', 'usr1', '치킨 시켰는데 날개 두 조각 실종. 혹시 배달 중에 날아간 거 아님?', 58, current_timestamp,
        current_timestamp),
       ('post4', 'usr1', '엘리베이터에서 모르는 사람이랑 동시에 "안녕하세요" 해서 혼자 민망했다. 나 혼잣말 한 거였음.', 31,
        current_timestamp, current_timestamp),
       ('post5', 'usr1', '오늘은 아무것도 안 했지만 피곤하다. 아마 존재 자체가 피로인 듯.', 65, current_timestamp,
        current_timestamp);

