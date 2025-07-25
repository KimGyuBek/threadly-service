drop table if exists comment_likes;
drop table if exists post_comments;
drop table if exists post_likes;
drop table if exists posts cascade;

drop table if exists user_profile;
drop table if exists users;
drop table if exists post_images;

--users
create table users
(
    user_id           varchar(50) primary key,
    user_name         varchar(255) not null,
    password          varchar(255) not null,
    email             varchar(255) not null,
    phone             varchar(50)  not null,
    user_type         varchar(50)  not null,
    status            varchar(20)  not null default 'ACTIVE',
    is_email_verified boolean      not null default false,
    created_at        timestamp    not null default current_timestamp,
    modified_at       timestamp    not null default current_timestamp
);

--user_profile
create table user_profile
(
    user_id           varchar(50) primary key,
    nickname          varchar(255) not null,
    status_message    varchar(255) not null,
    bio               varchar(255) not null,
    gender            varchar(50)  not null,
    profile_type      varchar(50)  not null default 'USER',
    profile_image_url varchar(255),
    created_at        timestamp    not null default current_timestamp,
    modified_at       timestamp    not null default current_timestamp,
    foreign key (user_id) references users (user_id) on delete cascade
);


--posts
create table posts
(
    post_id     varchar(50) not null primary key,
    user_id     varchar(50) not null,
    content     varchar(1000),
    view_count  int                  default 0,
    status      varchar(50) not null,
    created_at  timestamp   not null default current_timestamp,
    modified_at timestamp   not null default current_timestamp,
    foreign key (user_id) references users (user_id)
);

--post_likes
create table post_likes
(
    post_id    varchar(50) not null,
    user_id    varchar(50) not null,
    created_at timestamp   not null default current_timestamp,
    primary key (post_id, user_id),
    foreign key (post_id) references posts (post_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);

--post_comments
create table post_comments
(
    comment_id  varchar(50)   not null primary key,
    post_id     varchar(50)   not null,
    user_id     varchar(50)   not null,
    content     varchar(1000) not null,
    status      varchar(50)   not null,
    created_at  timestamp     not null default current_timestamp,
    modified_at timestamp     not null default current_timestamp,
    foreign key (post_id) references posts (post_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);

--comment_likes
create table comment_likes
(
    comment_id varchar(50) not null,
    user_id    varchar(50) not null,
    created_at timestamp   not null default current_timestamp,
    primary key (comment_id, user_id),
    foreign key (comment_id) references post_comments (comment_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);

--post_images
create table post_images
(
    post_image_id    varchar(255) not null,
    post_id          varchar(255),
    stored_file_name varchar(255) not null,
    image_order      int          not null default 0,
    image_url        varchar(255) not null default '/',
    status           varchar(50)  not null,
    created_at       timestamp    not null default current_timestamp,
    modified_at      timestamp    not null default current_timestamp,
    primary key (post_image_id),
    foreign key (post_id) references posts (post_id)
);

