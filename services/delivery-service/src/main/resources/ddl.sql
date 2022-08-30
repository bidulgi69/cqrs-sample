DROP TABLE IF EXISTS DELIVERIES;
CREATE TABLE DELIVERIES(
    id  varchar(255)    primary key,
    state   varchar(30) default 'PICKEDUP',
    order_id    varchar(255)    not null,
    customer_id varchar(255)    not null,
    rider_id    varchar(255)    not null,
    picked_up_time  timestamp   default current_timestamp,
    delivery_completed_time timestamp   default null,
    version int default 0
)