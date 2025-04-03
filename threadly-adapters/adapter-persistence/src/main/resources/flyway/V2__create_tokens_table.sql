drop table if exists tokens;
create table tokens
(
    token_id                 varchar(255) not null,
    user_id                  varchar(255) not null,
    access_token             varchar(255) not null,
    refresh_token            varchar(255) not null,
    access_token_expires_at  timestamp    not null,
    refresh_token_expires_at timestamp    not null,
    created_at               timestamp    not null default current_timestamp,
    modified_at              timestamp    not null default current_timestamp
);
