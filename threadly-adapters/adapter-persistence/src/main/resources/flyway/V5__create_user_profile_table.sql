create table user_profile
(
    user_profile_id   varchar(50)  not null primary key,
    nickname          varchar(255) not null,
    status_message    varchar(255) not null,
    bio               varchar(255) not null,
    gender            varchar(50)  not null,
    profile_type      varchar(50)  not null default 'USER',
    profile_image_url varchar(255),
    created_at        timestamp    not null default current_timestamp,
    modified_at       timestamp    not null default current_timestamp
);