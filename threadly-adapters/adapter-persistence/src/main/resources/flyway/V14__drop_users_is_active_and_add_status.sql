-- is_active 컬럼 삭제
alter table users drop if exists is_active;

-- status 컬럼 추가
alter table users
    add column status varchar(20) not null default 'ACTIVE';

