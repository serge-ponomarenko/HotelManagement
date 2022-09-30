drop table if exists rooms CASCADE;

create table IF NOT EXISTS rooms
(
    room_id     serial
        primary key,
    number      text,
    occupancy   integer,
    category_id serial
        constraint rooms_categories_category_id_fk
            references categories
            on update cascade on delete cascade,
    created_at  timestamp default now(),
    price       numeric
);

INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('102', 2, 2, '2022-08-26 12:33:48.232502', 70);
INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('201', 1, 4, '2022-08-26 12:33:48.232502', 94);
INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('202', 1, 4, '2022-08-26 12:33:48.232502', 94);
INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('203', 2, 4, '2022-08-26 12:33:48.232502', 126);
INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('204', 2, 4, '2022-08-26 12:33:48.232502', 132);
INSERT INTO rooms (number, occupancy, category_id, created_at, price)
VALUES ('301', 1, 3, '2022-08-26 12:33:48.232502', 81);

drop table if exists rooms_tr CASCADE;

create table IF NOT EXISTS rooms_tr
(
    room_tr_id  serial
        primary key,
    room_id     integer
        constraint rooms_tr_rooms_room_id_fk
            references rooms
            on update cascade on delete cascade,
    locale_id   integer
        constraint rooms_tr_locales_locale_id_fk
            references locales
            on update cascade on delete cascade,
    description text,
    name        text
);

INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (1, 1, e'1 single bed<br>
A spacious room with a private bathroom, cable TV and a safety deposit box.<br>
<br>
Room Facilities: <br>
• Linens • Tea/Coffee maker • Air conditioning • Safe • Desk<br>
In your private bathroom:<br>
• Free toiletries • Toilet • Bathtub or shower • Towels', 'Business Single Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (1, 2, e'1 односпальне ліжко<br>
Просторий номер з окремою ванною кімнатою, кондиціонером і диваном.<br>
<br>
Оснащення номерів:<br>
• Постільна білизна • Чайник/кавоварка • Кондиціонер • Сейф • Письмовий стіл<br>
У вашій приватній ванній кімнаті:<br>
• Безкоштовні туалетно-косметичні засоби • Туалет • Ванна або душ • Рушники', 'Одномісний номер бізнес-класу');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (2, 1, e'2 single beds<br>
This twin/double room features a mini-bar, soundproofing and satellite TV.<br>
<br>
Room Facilities: <br>
• Linens • Tea/Coffee maker • Air conditioning • Safe • Hypoallergenic<br>
In your private bathroom:<br>
• Free toiletries • Toilet • Bathtub or shower • Towels • Hairdryer • Toilet paper', 'Standart Double Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (2, 2, e'2 односпальні ліжка<br>
Звуконепроникний двомісний номер/номер Твін з міні-баром і супутниковим телебаченням.<br>
<br>
Оснащення номерів:<br>
• Постільна білизна • Чайник/кавоварка • Кондиціонер • Безпечний • Гіпоалергенний<br>
У вашій приватній ванній кімнаті:<br>
• Безкоштовні туалетно-косметичні засоби • Туалет • Ванна або душ • Рушники • Фен • Туалетний папір', 'Стандартний двомісний номер');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (3, 1, e'1 king bed<br>
This double room has a toaster, sofa and tea/coffee maker.<br>
<br>
Room Facilities:<br>
• Linens • Tea/Coffee maker • Air conditioning • Safe • Heating • Refrigerator • Coffee machine • Toaster<br>
In your private bathroom:<br>
• Free toiletries • Shower • Towels • Hairdryer', 'President Double Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (3, 2, e'1 ліжко розміру "queen-size".<br>
2 односпальні ліжка<br>
У цій квартирі є дві двомісні кімнати з широкоекранним телевізором, халатом і кондиціонером.<br>
<br>
Зручності в номері:<br>
• Кондиціонер • Окрема ванна кімната • Телевізор • Wi-Fi • За запитом можна встановити 2 окремі ліжка<br>
У вашій приватній ванній кімнаті:<br>
• Біде • Безкоштовні туалетно-косметичні засоби • Туалет • Ванна або душ • Рушники • Фен', 'Чотиримісний номер Люкс');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (4, 1, e'2 single beds<br>
This twin/double room features a mini-bar, soundproofing and satellite TV.<br>
<br>
Room Facilities: <br>
• Satellite channels • Flat-screen TV • Socket near the bed • Clothes rack • Wake-up service<br>
In your private bathroom:<br>
• Free toiletries • Hairdryer • Bathtub or shower • Towels', 'Business Double Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (4, 2, e'1 королівське ліжко<br>
У цьому двомісному номері є тостер, диван і все необхідне для приготування чаю/кави.<br>
<br>
Зручності в номері:<br>
• Постільна білизна • Чайник/кавоварка • Кондиціонер • Сейф • Опалення • Холодильник • Кавоварка • Тостер<br>
У вашій приватній ванній кімнаті:<br>
• Безкоштовні туалетно-косметичні засоби • Душ • Рушники • Фен', 'Президентський двомісний номер');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (5, 1, e'1 single bed<br>
A spacious room with a private bathroom, air conditioning and sofa.<br>
<br>
Room Facilities: <br>
• Linens • Tea/Coffee maker • Air conditioning • Safe • Desk<br>
In your private bathroom:<br>
• Free toiletries • Toilet • Bathtub or shower • Towels', 'Business Single Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (5, 2, e'1 двоспальне ліжко<br>
2 односпальні ліжка<br>
Тримісний номер із зоною відпочинку оснащений електричним чайником та усім необхідним для приготування чаю/кави.<br>
<br>
Оснащення номерів:<br>
• Опалення • Холодильник • Кавоварка • Електрочайник<br>
У вашій приватній ванній кімнаті:<br>
• Туалетний папір • Фен • Безкоштовні туалетно-косметичні засоби • Рушники • Туалет • Ванна або душ', 'Тримісний стандартний номер');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (6, 1, e'1 single bed<br>
This single room features a mini-bar, soundproofing and satellite TV.<br>
<br>
Room Facilities: <br>
• Linens • Tea/Coffee maker • Air conditioning • Safe • Hypoallergenic<br>
In your private bathroom:<br>
• Free toiletries • Toilet • Bathtub or shower • Towels • Hairdryer • Toilet paper', 'Standart Single Room');
INSERT INTO rooms_tr (room_id, locale_id, description, name)
VALUES (6, 2, e'1 ліжко розміру "queen-size".<br>
Двомісний номер з телевізором з плоским екраном, халатом і електричним чайником.<br>
<br>
Зручності в номері:<br>
• Кондиціонер • Окрема ванна кімната • Телевізор • Wi-Fi • За запитом можна встановити 2 окремі ліжка<br>
У вашій приватній ванній кімнаті:<br>
• Біде • Безкоштовні туалетно-косметичні засоби • Туалет • Ванна або душ • Рушники • Фен', 'Двомісний номер Люкс');

drop table if exists room_images CASCADE;

create table IF NOT EXISTS room_images
(
    room_image_id serial
        primary key,
    room_id       integer
        constraint room_images_rooms_room_id_fk
            references rooms
            on update cascade on delete cascade,
    path          text,
    created_at    timestamp default now()
);

INSERT INTO room_images (room_id, path, created_at)
VALUES (1, './uploads/101/202319955.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (1, './uploads/101/202320042.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (2, './uploads/101/263082417.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (2, './uploads/102/123091697.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (3, './uploads/102/153020256.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (3, './uploads/102/153020259.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (4, './uploads/102/153020262.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (4, './uploads/102/202320042.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (5, './uploads/201/199540635.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (5, './uploads/201/199540743.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (6, './uploads/201/199540762.jpg', '2022-08-26 13:00:29.063140');
INSERT INTO room_images (room_id, path, created_at)
VALUES (6, './uploads/201/199540804.jpg', '2022-08-26 13:00:29.063140');

