DROP TABLE IF EXISTS COOKS;
CREATE TABLE COOKS(
    id  varchar(255)    primary key,
    state   varchar(30) default 'PREPARING',
    order_id    varchar(255)    not null,
    ticket_id   varchar(255)    not null,
    accept_time timestamp   default current_timestamp,
    ready_time  timestamp   default null,
    version int default 0
)