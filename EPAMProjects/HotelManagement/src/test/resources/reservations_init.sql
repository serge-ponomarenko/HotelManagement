drop table if exists reservations CASCADE;

create table IF NOT EXISTS reservations
(
    reservation_id serial
        primary key,
    checkin_date   date    not null,
    checkout_date  date    not null,
    status_id      integer not null
        constraint reservations_statuses_status_id_fk
            references statuses
            on update cascade on delete cascade,
    user_id        integer not null
        constraint reservations_users_user_id_fk
            references users
            on update cascade on delete cascade,
    created_at     timestamp default now(),
    persons        integer not null,
    price          numeric
);

INSERT INTO reservations (checkin_date, checkout_date, status_id, user_id, created_at, persons, price)
VALUES ('2022-08-24', '2022-09-20', 2, 1, '2022-09-23 15:31:20.723447', 1, 2538);
INSERT INTO reservations (checkin_date, checkout_date, status_id, user_id, created_at, persons, price)
VALUES ('2022-08-25', '2022-09-07', 3, 1, '2022-09-23 15:37:23.316354', 1, 3029);
INSERT INTO reservations (checkin_date, checkout_date, status_id, user_id, created_at, persons, price)
VALUES ('2022-09-05', '2022-09-18', 6, 2, '2022-09-23 15:39:17.904036', 1, 3029);

drop table if exists reservations_rooms CASCADE;

create table IF NOT EXISTS reservations_rooms
(
    reservation_id integer not null
        constraint reservations_rooms_reservations_reservation_id_fk
            references reservations
            on update cascade on delete cascade,
    room_id        integer not null
        constraint reservations_rooms_rooms_room_id_fk
            references rooms
            on update cascade on delete cascade,
    created_at     timestamp default now(),
    constraint reservations_rooms_pk
        primary key (reservation_id, room_id)
);

INSERT INTO reservations_rooms (reservation_id, room_id, created_at)
VALUES (1, 1, '2022-09-23 15:13:51.720413');
INSERT INTO reservations_rooms (reservation_id, room_id, created_at)
VALUES (1, 2, '2022-09-23 15:26:49.791275');
INSERT INTO reservations_rooms (reservation_id, room_id, created_at)
VALUES (2, 3, '2022-09-23 15:28:18.111503');
INSERT INTO reservations_rooms (reservation_id, room_id, created_at)
VALUES (2, 4, '2022-09-23 15:28:28.879044');
INSERT INTO reservations_rooms (reservation_id, room_id, created_at)
VALUES (3, 5, '2022-09-23 15:29:11.496992');

