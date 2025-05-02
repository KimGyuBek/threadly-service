alter table users
    add column user_profile_id varchar(255),
    add constraint fk_user_profile
        foreign key (user_profile_id) references user_profile(user_profile_id);



