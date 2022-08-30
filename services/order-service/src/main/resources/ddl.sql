DROP TABLE IF EXISTS ORDERS;
CREATE TABLE ORDERS(
    id  varchar(255)    primary key,
    state varchar(30)   not null,
    customer_id varchar(255)    not null,
    restaurant_id   varchar(255)    not null,
    payment_as_json varchar(512)    not null,
    order_items_as_json varchar(2048)   not null,
    version int default 0
);

/**
    Use "api.core.outbox.Outbox" class as an entity class.
    When using this class, R2dbcEntityTemplate binds the name of the table to lowercase when querying.
    So you should create table name in lowercase.
 */
DROP TABLE IF EXISTS outbox;
CREATE TABLE outbox(
    id  int primary key auto_increment,
    topic   varchar(64) not null,
    event_as_json   varchar(1024)    not null
);