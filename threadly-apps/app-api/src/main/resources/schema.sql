drop table if exists users;
create table users
(
    user_id     varchar(255) not null,
    user_name   varchar(255) not null,
    password    varchar(255) not null,
    email       varchar(255) not null,
    phone       varchar(50)  not null,
    user_type   varchar(50)  not null,
    is_active   bool         not null,
    is_email_verified bool not null default false,
    user_profile_id varchar(255) ,
    created_at  timestamp    not null default current_timestamp,
    modified_at timestamp    not null default current_timestamp
);

