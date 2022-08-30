DROP TABLE IF EXISTS PAYMENTS;
CREATE TABLE PAYMENTS(
    id  varchar(255)    primary key,
    state   varchar(30) default 'PENDING',
    order_id    varchar(255)    unique  not null,
    customer_id    varchar(255)    not null,
    restaurant_id   varchar(255)    not null,
    payment_as_json varchar(512)   not null,
    version int default 0
);