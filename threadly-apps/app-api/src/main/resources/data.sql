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

-- 게시글 (총 20개)
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
        current_timestamp),
       ('post6', 'usr1', '버스 놓쳤는데 기사님이 멈춰주셨다... 오늘 운 다 쓴 듯', 21, current_timestamp,
        current_timestamp),
       ('post7', 'usr2', '비 오는 날엔 파전이지. 근데 집에 부침가루 없음ㅋ', 33, current_timestamp, current_timestamp),
       ('post8', 'usr1', '모닝 커피 마셨는데 벌써 세 번째야. 각성 완료.', 44, current_timestamp, current_timestamp),
       ('post9', 'usr2', '헬스장 갔다가 떡볶이 먹고 옴. 의미 없지?', 25, current_timestamp, current_timestamp),
       ('post10', 'usr4', '지하철에서 책 읽다가 울 뻔함. 민망할 뻔', 15, current_timestamp, current_timestamp),
       ('post11', 'usr5', '라면에 계란 넣었더니 신이 된 느낌이었다.', 18, current_timestamp, current_timestamp),
       ('post12', 'usr1', '편의점 갔는데 계산하고 나서 젓가락 안 집었음. 절망', 29, current_timestamp,
        current_timestamp),
       ('post13', 'usr4', '햇살 좋은 날엔 무조건 산책. 인증샷은 없음.', 40, current_timestamp, current_timestamp),
       ('post14', 'usr5', '아이스크림 사러 나갔다가 치킨까지 사옴. 충동의 끝', 32, current_timestamp, current_timestamp),
       ('post15', 'usr1', '친구랑 약속 잡았는데 서로 다른 지점에 옴ㅋㅋ', 50, current_timestamp, current_timestamp),
       ('post16', 'usr2', '지갑 안 가져왔는데 계산은 네이버페이로 함. 갓-테크', 19, current_timestamp,
        current_timestamp),
       ('post17', 'usr4', '버스에서 자다가 종점까지 감. 역주행 중.', 24, current_timestamp, current_timestamp),
       ('post18', 'usr5', '분명 과일 사러 나갔는데 과자가 더 많음', 39, current_timestamp, current_timestamp),
       ('post19', 'usr1', '세탁기 돌려놓고 까먹었다. 3시간째 담가놓음', 42, current_timestamp, current_timestamp),
       ('post20', 'usr2', '엘베 버튼 누르고 다른 층 올라간 사람 누구야...', 27, current_timestamp, current_timestamp);

