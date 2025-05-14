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
values ('1',
        'user_email_verified',
        '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'user_email_verified@test.com',
        '123-1234-1234',
        'USER',
        true,
        true,
        'up1',
        '2025-04-22 00:55:59.733403',
        '2025-04-22 00:55:59.733403');


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
values ('2',
        'user_email_not_verified',
        '$2a$10$ETO27eRm0V3M93i00eCNoeHupoHzlKqhbGEhfP.Ej8mXgQSDPHS4e',
        'user_email_not_verified@test.com',
        '123-1234-1234',
        'USER',
        true,
        false,
        '2025-04-22 00:55:59.733403',
        '2025-04-22 00:55:59.733403');

