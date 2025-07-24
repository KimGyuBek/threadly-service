--users
alter table users drop constraint if exists fk_user_profile;
alter table users drop column if exists user_profile_id;

--user_profile
alter table user_profile drop column if exists user_profile_id;
alter table user_profile
    add column user_id varchar(50);
alter table user_profile
    add constraint user_profile_pkey primary key (user_id);
alter table user_profile
    add constraint fk_user_profile_user_id
        foreign key (user_id) references users (user_id) on delete cascade;




