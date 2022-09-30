drop table if exists locales CASCADE;

create table if not exists locales
(
    locale_id serial
        primary key,
    name      varchar(5) not null,
    icon_path text,
    full_name text
);

INSERT INTO locales (locale_id, name, icon_path, full_name) VALUES (1, 'en', 'flag-country-gb', 'English');
INSERT INTO locales (locale_id, name, icon_path, full_name) VALUES (2, 'uk', 'flag-country-ua', 'Українська');
