/*==============================================================*/
/* DBMS name:      SQLite                                       */
/* Created on:     18/03/2024 23:37:44                          */
/*==============================================================*/

-- Dropping tables
-- drop table if exists CREDIT_CARD;
-- drop table if exists CUSTOMER;
-- drop table if exists EVENT;
-- drop table if exists "ORDER";
-- drop table if exists PRODUCT;
-- drop table if exists ORDER_PRODUCT;
-- drop table if exists PURCHASE;
-- drop table if exists TICKET;
-- drop table if exists VOUCHER;

/*==============================================================*/
/* Table: CUSTOMER                                              */
/*==============================================================*/
create table if not exists CUSTOMER
(
    CUSTOMER_ID                    varchar(128)                   not null,
    USERNAME                       varchar(512),
    PASSWORD                       text,
    TAX_NUMBER                     bigint,
    PUBLIC_KEY                     text,
    NAME                           varchar(1024),
    primary key (CUSTOMER_ID)
);

/*==============================================================*/
/* Table: CREDIT_CARD                                           */
/*==============================================================*/
create table if not exists CREDIT_CARD
(
    CREDIT_CARD_ID                 integer                        primary key autoincrement,
    CUSTOMER_ID                    varchar(128)                   not null,
    TYPE                           varchar(1024),
    NUMBER                         varchar(1024),
    VALIDITY                       date,
    foreign key (CUSTOMER_ID) references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
);

/*==============================================================*/
/* Table: EVENT                                                 */
/*==============================================================*/
create table if not exists EVENT
(
    EVENT_ID                       integer                        primary key autoincrement,
    NAME                           varchar(1024),
    DATE                           text,
    PICTURE                        blob, -- Changed to BLOB data type for image storage
    PRICE                          real
);

/*==============================================================*/
/* Table: "ORDER"                                               */
/*==============================================================*/
create table if not exists "ORDER"
(
    ORDER_ID                       integer                        primary key autoincrement,
    CUSTOMER_ID                    varchar(128)                   not null,
    DATE                           text,
    PAID                           bool,
    PICKED_UP                      bool,
    TOTAL_PRICE                    real,
    foreign key (CUSTOMER_ID) references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
);

/*==============================================================*/
/* Table: PRODUCT                                               */
/*==============================================================*/
create table if not exists PRODUCT
(
    PRODUCT_ID                     integer                        primary key autoincrement,
    AVAILABLE                      bool,
    NAME                           varchar(1024),
    DESCRIPTION                    varchar(1024),
    PRICE                          real
);

/*==============================================================*/
/* Table: ORDER_PRODUCT                                         */
/*==============================================================*/
create table if not exists ORDER_PRODUCT
(
    PRODUCT_ID                     integer                        not null,
    ORDER_ID                       integer                        not null,
    QUANTITY                       integer                        not null,
    primary key (PRODUCT_ID, ORDER_ID),
    foreign key (PRODUCT_ID) references PRODUCT (PRODUCT_ID) on delete cascade on update cascade,
    foreign key (ORDER_ID) references "ORDER" (ORDER_ID) on delete cascade on update cascade
);

/*==============================================================*/
/* Table: PURCHASE                                              */
/*==============================================================*/
create table if not exists PURCHASE
(
    PURCHASE_ID                    integer                        primary key autoincrement,
    CUSTOMER_ID                    varchar(128)                   not null,
    DATE                           text,
    TOTAL_PRICE                    real,
    foreign key (CUSTOMER_ID) references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
);

/*==============================================================*/
/* Table: TICKET                                                */
/*==============================================================*/
create table if not exists TICKET
(
    TICKET_ID                      varchar(128)                   not null,
    PURCHASE_ID                    integer                        not null,
    EVENT_ID                       integer                        not null,
    PURCHASE_DATE                  text,
    USED                           bool,
    QRCODE                         text,
    PLACE                          varchar(1024),
    primary key (TICKET_ID),
    foreign key (PURCHASE_ID) references PURCHASE (PURCHASE_ID) on delete cascade on update cascade,
    foreign key (EVENT_ID) references EVENT (EVENT_ID) on delete cascade on update cascade
);

/*==============================================================*/
/* Table: VOUCHER                                               */
/*==============================================================*/
create table if not exists VOUCHER
(
    VOUCHER_ID                     varchar(128)                   not null,
    CUSTOMER_ID                    varchar(128)                   not null,
    PRODUCT_ID                     integer,
    ORDER_ID                       integer,
    TYPE                           varchar(1024),
    DESCRIPTION                    varchar(1024),
    REDEEMED                       bool,
    primary key (VOUCHER_ID),
    foreign key (CUSTOMER_ID) references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade,
    foreign key (ORDER_ID) references "ORDER" (ORDER_ID) on delete cascade on update cascade,
    foreign key (PRODUCT_ID) references PRODUCT (PRODUCT_ID) on delete cascade on update cascade
);
