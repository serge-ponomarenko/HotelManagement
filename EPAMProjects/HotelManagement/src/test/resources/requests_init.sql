drop table if exists reservation_requests CASCADE;

create table IF NOT EXISTS reservation_requests
(
    reservation_request_id serial
        primary key,
    checkin_date           date    not null,
    checkout_date          date    not null,
    persons                integer not null,
    rooms                  integer not null,
    additional_information text,
    created_at             timestamp default now(),
    reservation_id         integer
        constraint reservation_requests_reservations_reservation_id_fk
            references reservations
            on update cascade on delete cascade,
    user_id                integer
        constraint reservation_requests_users_user_id_fk
            references users
            on update cascade on delete cascade
);

INSERT INTO reservation_requests (checkin_date, checkout_date, persons, rooms, additional_information, created_at, reservation_id, user_id)
VALUES ('2022-09-30', '2022-10-06', 5, 3, '', '2022-09-23 15:58:37.175114', 1, 1);
INSERT INTO reservation_requests (checkin_date, checkout_date, persons, rooms, additional_information, created_at, reservation_id, user_id)
VALUES ('2022-09-23', '2022-09-30', 4, 2, '', '2022-09-23 16:00:49.590554', 2, 2);
INSERT INTO reservation_requests (checkin_date, checkout_date, persons, rooms, additional_information, created_at, reservation_id, user_id)
VALUES ('2022-09-01', '2022-09-12', 1, 2, 'New request', '2022-09-23 16:03:36.124673', null, 3);

drop table if exists reservation_requests_categories CASCADE;

create table IF NOT EXISTS reservation_requests_categories
(
    reservation_request_id integer not null
        constraint reservation_requests_requests_linked_fk
            references reservation_requests
            on update cascade on delete cascade,
    category_id            integer not null
        constraint reservation_requests_categories_linked_fk
            references categories
            on update cascade on delete cascade,
    constraint reservation_requests_categories_pk
        primary key (reservation_request_id, category_id)
);


INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (1, 1);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (1, 2);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (1, 3);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (2, 4);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (3, 1);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (3, 3);
INSERT INTO reservation_requests_categories (reservation_request_id, category_id) VALUES (3, 5);
