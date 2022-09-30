drop table if exists categories CASCADE;

create table IF NOT EXISTS categories
(
    category_id serial
        constraint room_categories_pkey
            primary key,
    created_at  timestamp default now()
);

INSERT INTO categories (created_at) VALUES ('2022-08-26 11:07:23');
INSERT INTO categories (created_at) VALUES ('2022-08-26 11:07:23');
INSERT INTO categories (created_at) VALUES ('2022-08-26 11:07:23');
INSERT INTO categories (created_at) VALUES ('2022-08-26 11:07:23');
INSERT INTO categories (created_at) VALUES ('2022-08-26 11:07:23');

drop table if exists categories_tr CASCADE;

create table IF NOT EXISTS categories_tr
(
    category_tr_id serial
        primary key,
    category_id    integer
        constraint categories_tr_categories_category_id_fk
            references categories
            on update cascade on delete cascade,
    locale_id      integer
        constraint categories_tr_locales_locale_id_fk
            references locales
            on update cascade on delete cascade,
    name           text,
    description    text
);

INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (1, 1, 'Economy', 'Minimal amenities for a short stay');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (2, 1, 'Standart', 'Standard room with TV and refrigerator');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (3, 1, 'Business', 'Room for comfortable business travel');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (4, 1, 'Luxe', 'Superior comfort room with air conditioning');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (5, 1, 'President', 'Super comfortable apartments for an unforgettable vacation');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (1, 2, 'Економ', 'Мінімальні зручності для короткочасного перебування');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (2, 2, 'Стандарт', 'Стандартний номер з ТВ та холодильником');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (3, 2, 'Бізнес', 'Номер для комфортного забезпечення бізнес подорожі');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (4, 2, 'Люкс', 'Номер підвищеного комфорту з кондиціонером');
INSERT INTO categories_tr (category_id, locale_id, name, description) VALUES (5, 2, 'Президентський', 'Супер комфортні апартаменти для незабутнього відпочинку');

