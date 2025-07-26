-- create user_profile_images
drop table if exists user_profile_images;

create table user_profile_images
(
    user_profile_image_id varchar(50)  not null,
    user_id               varchar(50),
    stored_file_name      varchar(255) not null,
    image_url             varchar(255) not null,
    status                varchar(50)  not null,
    created_at            timestamp    not null default current_timestamp,
    modified_at           timestamp    not null default current_timestamp,
    primary key (user_profile_image_id),
    foreign key (user_id) references users (user_id)
);
