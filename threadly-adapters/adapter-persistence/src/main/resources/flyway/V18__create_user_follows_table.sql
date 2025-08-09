-- create user_follows table
create table user_follows
(
    follow_id    varchar(50) not null,
    follower_id  varchar(50) not null,
    following_id varchar(50) not null,
    status       varchar(50) not null,
    primary key (follow_id),
    created_at   timestamp   not null default current_timestamp,
    modified_at  timestamp   not null default CURRENT_TIMESTAMP,
    foreign key (follower_id) references users (user_id),
    foreign key (following_id) references users (user_id),
    constraint uq_follower_following unique (follower_id, following_id)
);
