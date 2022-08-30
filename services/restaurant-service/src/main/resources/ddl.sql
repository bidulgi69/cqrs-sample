DROP TABLE IF EXISTS TICKETS;
CREATE TABLE TICKETS(
    id  varchar(255)    primary key,
    state   varchar(30) default 'PENDING',
    order_id    varchar(255)    unique  not null,
    restaurant_id   varchar(255)    not null,
    accept_time timestamp   default current_timestamp,
    ready_time  timestamp   default null,
    preparing_time  int default null,
    picked_up_time  timestamp   default null,
    order_items_as_json varchar(2048)   not null,
    version int default 0
);

DROP TABLE IF EXISTS MENUS;
CREATE TABLE MENUS(
    id  varchar(255)    primary key,
    restaurant_id   varchar(255)    not null,
    name    varchar(256)    not null,
    description varchar(1024)   default '',
    price   int default 0,
    rating  float   default 0,
    version int default 0
);

INSERT INTO MENUS(id, restaurant_id, name, description, price, rating) VALUES
(
'1',
'1',
'Veggie KimBap',
'KimBap with vegetables (no meat)',
3500,
3.6
),
(
'2',
'1',
'Shin Ramyun',
'A spicy noodle',
4000,
3.2
);

DROP TABLE IF EXISTS RESTAURANTS;
CREATE TABLE RESTAURANTS(
    id  varchar(255)    primary key,
    name    varchar(256)    not null,
    address_as_json varchar(256)    not null,
    version int default 0
);

INSERT INTO RESTAURANTS(id, name, address_as_json) VALUE
(
'1',
'KimBab Heaven',
'{"lat":37.501964,"long":126.883027}'
);