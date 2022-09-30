drop table if exists user_roles CASCADE;

create table if not exists user_roles
(
    role_id serial
        primary key,
    name    text
);

INSERT INTO user_roles (role_id, name)
VALUES (1, 'ADMINISTRATOR');
INSERT INTO user_roles (role_id, name)
VALUES (2, 'MANAGER');
INSERT INTO user_roles (role_id, name)
VALUES (3, 'USER');

drop table if exists users CASCADE;

create table if not exists users
(
    user_id       serial
        primary key,
    email         text
        constraint users_login_key
            unique,
    hash_password varchar(60),
    first_name    text,
    last_name     text,
    role_id       integer
        constraint users_user_roles_role_id_fk
            references user_roles
            on update cascade on delete cascade,
    created_at    timestamp default now()
);

--encrypted password is "qwertyuiop123"

INSERT INTO users (email, hash_password, first_name, last_name, role_id, created_at)
VALUES ('pat_mair@gmail.com', '$2a$08$yvuqGKWCJ.I1/gttH/4KGuQfyu2Kos94HdcvhswQlZCAGvL3xtRxe',
        'Patryk', 'Mair', 3, '2022-09-21 14:19:19');
INSERT INTO users (email, hash_password, first_name, last_name, role_id, created_at)
VALUES ('f.seymour@gmail.com', '$2a$08$OHi/vdy58jpfpXmNA7JG8.12rK30RREC1VYgBqQWBvh/M2BpzlqJW',
        'Fateh', 'Seymour', 2, '2022-09-21 14:22:52');
INSERT INTO users (email, hash_password, first_name, last_name, role_id, created_at)
VALUES ('s.stefaniv@gmail.com', '$2a$08$gpKBviVZvf9cIb9NaN94y.Am9b8ejo3RyxOHcheSJtGCi3w7BEW2K',
        'Sergiy', 'Ponomarenko', 1, '2022-09-21 14:14:05');









