drop table if exists statuses CASCADE;

create table IF NOT EXISTS statuses
(
    status_id serial
        primary key,
    name      varchar(50)
);

INSERT INTO statuses (status_id, name) VALUES (1, 'FREE');
INSERT INTO statuses (status_id, name) VALUES (2, 'BOOKED');
INSERT INTO statuses (status_id, name) VALUES (3, 'PAID');
INSERT INTO statuses (status_id, name) VALUES (4, 'BUSY');
INSERT INTO statuses (status_id, name) VALUES (5, 'UNAVAILABLE');
INSERT INTO statuses (status_id, name) VALUES (7, 'CANCELED');
INSERT INTO statuses (status_id, name) VALUES (6, 'COMPLETED');

drop table if exists statuses_tr CASCADE;

create table IF NOT EXISTS statuses_tr
(
    status_tr_id serial
        primary key,
    status_id    integer
        constraint statuses_tr_statuses_status_id_fk
            references statuses
            on update cascade on delete cascade,
    locale_id    integer
        constraint statuses_tr_locales_locale_id_fk
            references locales
            on update cascade on delete cascade,
    name         varchar(50)
);

INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (7, 2, 2, 'Зарезервований');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (6, 1, 2, 'Вільний');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (9, 4, 2, 'Занятий');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (8, 3, 2, 'Оплачений');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (10, 5, 2, 'Недоступний');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (12, 6, 2, 'Виконано');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (14, 7, 2, 'Скасовано');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (4, 4, 1, 'Busy');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (13, 7, 1, 'Canceled');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (11, 6, 1, 'Completed');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (1, 1, 1, 'Free');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (3, 3, 1, 'Paid');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (2, 2, 1, 'Booked');
INSERT INTO statuses_tr (status_tr_id, status_id, locale_id, name) VALUES (5, 5, 1, 'Unavailable');



