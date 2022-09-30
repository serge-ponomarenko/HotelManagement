drop table if exists user_settings CASCADE;

create table if not exists user_settings
(
    user_settings_id serial
        primary key,
    user_id          INTEGER
        unique,
    locale_id        INTEGER
        not null,
    created_at       TIMESTAMP default now(),
    hash             text,
    constraint USER_SETTINGS_LOCALES_LOCALE_ID_FK
        foreign key (locale_id) references locales
            on update cascade on delete cascade,
    constraint USER_SETTINGS_USERS_USER_ID_FK
        foreign key (user_id) references users
            on update cascade on delete cascade
);

INSERT INTO user_settings (user_id, locale_id, created_at, hash)
VALUES (1, 1, '2022-09-21 14:21:48', '1ec3fadba011470dbc2e');
INSERT INTO user_settings (user_id, locale_id, created_at, hash)
VALUES (2, 2, '2022-09-21 14:20:22', null);

