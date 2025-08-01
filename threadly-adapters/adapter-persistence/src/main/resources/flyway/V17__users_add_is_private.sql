-- add column is_private
alter table users
    add column is_private bool not null default false;

