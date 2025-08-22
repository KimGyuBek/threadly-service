drop table if exists comment_likes;
drop table if exists post_comments;
drop table if exists post_likes;
drop table if exists posts cascade;

drop table if exists user_follows;
drop table if exists user_profile;
drop table if exists users cascade;
drop table if exists post_images;
drop table if exists user_profile_images;

-- drop table if exists BATCH_JOB_INSTANCE;
-- drop table if exists BATCH_JOB_EXECUTION;
-- drop table if exists BATCH_JOB_EXECUTION_PARAMS;
-- drop table if exists BATCH_STEP_EXECUTION;
-- drop table if exists BATCH_STEP_EXECUTION_CONTEXT;
-- drop table if exists BATCH_JOB_EXECUTION_CONTEXT;

SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE;

SET REFERENTIAL_INTEGRITY TRUE;

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
    is_private        boolean      not null default false,
    created_at        timestamp    not null default current_timestamp,
    modified_at       timestamp    not null default current_timestamp
);

--user_profile
create table user_profile
(
    user_id        varchar(50) primary key,
    nickname       varchar(255) not null,
    status_message varchar(255) not null,
    bio            varchar(255) not null,
    gender         varchar(50)  not null,
    profile_type   varchar(50)  not null default 'USER',
    created_at     timestamp    not null default current_timestamp,
    modified_at    timestamp    not null default current_timestamp,
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
    modified_at timestamp   not null default current_timestamp
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

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT    NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

DROP SEQUENCE IF EXISTS BATCH_STEP_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP SEQUENCE IF EXISTS BATCH_JOB_SEQ;

CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;