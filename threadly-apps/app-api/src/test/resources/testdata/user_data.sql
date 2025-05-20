insert into user_profile(user_profile_id,
                         nickname,
                         status_message,
                         bio,
                         gender,
                         profile_type,
                         profile_image_url)
values ('up1',
        'coffee_cat',
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

insert into user_profile(user_profile_id,
                         nickname,
                         status_message,
                         bio,
                         gender,
                         profile_type,
                         profile_image_url)
values ('up2',
        'midnight_runner',
        'status message2',
        'bio2',
        'MALE',
        'USER',
        '/');
-- 사용자 4
-- 프로필 등록
insert into user_profile(user_profile_id, nickname, status_message, bio, gender, profile_type,
                         profile_image_url)
values ('up4', 'sunny_side', '빛처럼 살자', '아침형 인간입니다', 'FEMALE', 'USER', '/');

insert into users (user_id, user_name, password, email, phone, user_type, is_active,
                   is_email_verified, user_profile_id, created_at, modified_at)
values ('usr4', 'user4', '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'sunny@test.com', '111-2222-3333', 'USER', true, true, 'up4', current_timestamp,
        current_timestamp);

-- 사용자 5
insert into user_profile(user_profile_id, nickname, status_message, bio, gender, profile_type,
                         profile_image_url)
values ('up5', 'noodle_holic', '면 없인 못 살아', '맛집 탐방 중', 'MALE', 'USER', '/');

insert into users (user_id, user_name, password, email, phone, user_type, is_active,
                   is_email_verified, user_profile_id, created_at, modified_at)
values ('usr5', 'user5', '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'noodle@test.com', '555-6666-7777', 'USER', true, true, 'up5', current_timestamp,
        current_timestamp);

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

