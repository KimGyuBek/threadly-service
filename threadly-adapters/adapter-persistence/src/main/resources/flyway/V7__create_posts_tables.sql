create table posts
(
    post_id     varchar(50) not null primary key,
    user_id     varchar(50) not null,
    content     text,
    view_count  int                  default 0,
    created_at  timestamp   not null default current_timestamp,
    modified_at timestamp   not null default current_timestamp,
    foreign key (user_id) references users (user_id)
);

create table post_likes
(
    post_id    varchar(50) not null,
    user_id    varchar(50) not null,
    created_at timestamp   not null default current_timestamp,
    primary key (post_id, user_id),
    foreign key (post_id) references posts (post_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);

create table post_comments
(
    comment_id  varchar(50) not null primary key,
    post_id     varchar(50) not null,
    user_id     varchar(50) not null,
    content     text        not null,
    created_at  timestamp   not null default current_timestamp,
    modified_at timestamp   not null default current_timestamp,
    foreign key (post_id) references posts (post_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);

create table comment_likes
(
    comment_id varchar(50) not null,
    user_id    varchar(50) not null,
    created_at timestamp   not null default current_timestamp,
    primary key (comment_id, user_id),
    foreign key (comment_id) references post_comments (comment_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);







