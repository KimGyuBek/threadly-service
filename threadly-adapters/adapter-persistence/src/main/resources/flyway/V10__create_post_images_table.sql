drop table if exists post_images;

create table post_images
(
    post_image_id    varchar(255) not null,
    post_id          varchar(255) not null,
    stored_file_name varchar(255) not null,
    image_order      int          not null default 1,
    image_url        varchar(255) not null default '/',
    created_at       timestamp    not null default current_timestamp,
    primary key (post_image_id),
    foreign key (post_id) references posts (post_id)
);
