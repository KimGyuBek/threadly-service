-- deleted_at 삭제
alter table post_images
drop
column deleted_at;

-- status 추가
alter table post_images
    add column status varchar(50) not null;

-- modified_at 추가
alter table post_images
    add column modified_at timestamp not null default current_timestamp;


