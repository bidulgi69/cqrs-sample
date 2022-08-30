DROP TABLE IF EXISTS CUSTOMERS;
CREATE TABLE CUSTOMERS(
    id  varchar(255)    primary key,
    firstname   varchar(128)    not null,
    lastname    varchar(128)    not null,
    fullname    varchar(256)    default null,
    address_as_json varchar(256)    not null,
    card_as_json    varchar(512)    not null,
    version     int default 0
);

INSERT INTO CUSTOMERS(id, firstname, lastname, fullname, address_as_json, card_as_json) VALUE (
'1',
'BeomSoo',
'Jeon',
'BeomSoo Jeon',
'{"lat":37.456257,"long":126.705208}',
'{"cvc":"111","number":"0123456789","yy":"24","mm":"01"}'
);