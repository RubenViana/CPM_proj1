/*==============================================================*/
/* DBMS name:      MySQL 4.0                                    */
/* Created on:     19/03/2024 00:02:29                          */
/*==============================================================*/


drop index CUSTOMER_CREDITCARD_FK on CREDIT_CARD;

drop table if exists CREDIT_CARD;

drop table if exists CUSTOMER;

drop table if exists EVENT;

drop index CUSTOMER_RDER_FK on "ORDER";

drop table if exists "ORDER";

drop index ORDER_ORDER_PRODUCT_FK on ORDER_PRODUCT;

drop index PRODUCT_ORDER_PRODUCT_FK on ORDER_PRODUCT;

drop table if exists ORDER_PRODUCT;

drop table if exists PRODUCT;

drop index CUSTOMER_PURCHASE_FK on PURCHASE;

drop table if exists PURCHASE;

drop index EVENT_TICKET_FK on TICKET;

drop index PURCHASE_TICKET_FK on TICKET;

drop table if exists TICKET;

drop index PRODUCT_VOUCHER_FK on VOUCHER;

drop index CAFETERIAORDER_VOUCHER_FK on VOUCHER;

drop index CUSTOMER_VOUCHER_FK on VOUCHER;

drop table if exists VOUCHER;

/*==============================================================*/
/* Table: CUSTOMER                                              */
/*==============================================================*/
create table CUSTOMER
(
   CUSTOMER_ID                    varchar(128)                   not null,
   USERNAME                       varchar(512),
   PASSWORD                       text,
   TAX_NUMBER                     bigint,
   PUBLIC_KEY                     text,
   NAME                           varchar(1024),
   primary key (CUSTOMER_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table: CREDIT_CARD                                           */
/*==============================================================*/
create table CREDIT_CARD
(
   CREDIT_CARD_ID                 int                            not null AUTO_INCREMENT,
   CUSTOMER_ID                    varchar(128)                   not null,
   TYPE                           varchar(1024),
   NUMBER                         varchar(1024),
   VALIDITY                       date,
   primary key (CREDIT_CARD_ID),
   constraint FK_CUSTOMER_CREDITCARD foreign key (CUSTOMER_ID)
      references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: CUSTOMER_CREDITCARD_FK                                */
/*==============================================================*/
create index CUSTOMER_CREDITCARD_FK on CREDIT_CARD
(
   CUSTOMER_ID
);

/*==============================================================*/
/* Table: EVENT                                                 */
/*==============================================================*/
create table EVENT
(
   EVENT_ID                       int                            not null AUTO_INCREMENT,
   NAME                           varchar(1024),
   DATE                           datetime,
   PICTURE                        longblob,
   PRICE                          float,
   primary key (EVENT_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table: "ORDER"                                               */
/*==============================================================*/
create table "ORDER"
(
   ORDER_ID                       int                            not null AUTO_INCREMENT,
   CUSTOMER_ID                    varchar(128)                   not null,
   DATE                           datetime,
   PAID                           bool,
   PICKED_UP                      bool,
   TOTAL_PRICE                    float,
   primary key (ORDER_ID),
   constraint FK_CUSTOMER_RDER foreign key (CUSTOMER_ID)
      references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: CUSTOMER_RDER_FK                                      */
/*==============================================================*/
create index CUSTOMER_RDER_FK on "ORDER"
(
   CUSTOMER_ID
);

/*==============================================================*/
/* Table: PRODUCT                                               */
/*==============================================================*/
create table PRODUCT
(
   PRODUCT_ID                     int                            not null AUTO_INCREMENT,
   AVAILABLE                      bool,
   NAME                           varchar(1024),
   DESCRIPTION                    varchar(1024),
   PRICE                          float,
   primary key (PRODUCT_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table: ORDER_PRODUCT                                         */
/*==============================================================*/
create table ORDER_PRODUCT
(
   PRODUCT_ID                     int                            not null,
   ORDER_ID                       int                            not null,
   QUANTITY                       int                            not null,
   primary key (PRODUCT_ID, ORDER_ID),
   constraint FK_PRODUCT_ORDER_PRODUCT foreign key (PRODUCT_ID)
      references PRODUCT (PRODUCT_ID) on delete cascade on update cascade,
   constraint FK_ORDER_ORDER_PRODUCT foreign key (ORDER_ID)
      references "ORDER" (ORDER_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: PRODUCT_ORDER_PRODUCT_FK                              */
/*==============================================================*/
create index PRODUCT_ORDER_PRODUCT_FK on ORDER_PRODUCT
(
   PRODUCT_ID
);

/*==============================================================*/
/* Index: ORDER_ORDER_PRODUCT_FK                                */
/*==============================================================*/
create index ORDER_ORDER_PRODUCT_FK on ORDER_PRODUCT
(
   ORDER_ID
);

/*==============================================================*/
/* Table: PURCHASE                                              */
/*==============================================================*/
create table PURCHASE
(
   PURCHASE_ID                    int                            not null AUTO_INCREMENT,
   CUSTOMER_ID                    varchar(128)                   not null,
   DATE                           datetime,
   TOTAL_PRICE                    float,
   primary key (PURCHASE_ID),
   constraint FK_CUSTOMER_PURCHASE foreign key (CUSTOMER_ID)
      references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: CUSTOMER_PURCHASE_FK                                  */
/*==============================================================*/
create index CUSTOMER_PURCHASE_FK on PURCHASE
(
   CUSTOMER_ID
);

/*==============================================================*/
/* Table: TICKET                                                */
/*==============================================================*/
create table TICKET
(
   TICKET_ID                      varchar(128)                   not null,
   PURCHASE_ID                    int                            not null,
   EVENT_ID                       int                            not null,
   PURCHASE_DATE                  datetime,
   USED                           bool,
   QRCODE                         text,
   PLACE                          varchar(1024),
   primary key (TICKET_ID),
   constraint FK_PURCHASE_TICKET foreign key (PURCHASE_ID)
      references PURCHASE (PURCHASE_ID) on delete cascade on update cascade,
   constraint FK_EVENT_TICKET foreign key (EVENT_ID)
      references EVENT (EVENT_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: PURCHASE_TICKET_FK                                    */
/*==============================================================*/
create index PURCHASE_TICKET_FK on TICKET
(
   PURCHASE_ID
);

/*==============================================================*/
/* Index: EVENT_TICKET_FK                                       */
/*==============================================================*/
create index EVENT_TICKET_FK on TICKET
(
   EVENT_ID
);

/*==============================================================*/
/* Table: VOUCHER                                               */
/*==============================================================*/
create table VOUCHER
(
   VOUCHER_ID                     varchar(128)                   not null,
   CUSTOMER_ID                    varchar(128)                   not null,
   PRODUCT_ID                     int,
   ORDER_ID                       int,
   TYPE                           varchar(1024),
   DESCRIPTION                    varchar(1024),
   REDEEMED                       bool,
   primary key (VOUCHER_ID),
   constraint FK_CUSTOMER_VOUCHER foreign key (CUSTOMER_ID)
      references CUSTOMER (CUSTOMER_ID) on delete cascade on update cascade,
   constraint FK_CAFETERIAORDER_VOUCHER foreign key (ORDER_ID)
      references "ORDER" (ORDER_ID) on delete cascade on update cascade,
   constraint FK_PRODUCT_VOUCHER foreign key (PRODUCT_ID)
      references PRODUCT (PRODUCT_ID) on delete cascade on update cascade
)
type = InnoDB;

/*==============================================================*/
/* Index: CUSTOMER_VOUCHER_FK                                   */
/*==============================================================*/
create index CUSTOMER_VOUCHER_FK on VOUCHER
(
   CUSTOMER_ID
);

/*==============================================================*/
/* Index: CAFETERIAORDER_VOUCHER_FK                             */
/*==============================================================*/
create index CAFETERIAORDER_VOUCHER_FK on VOUCHER
(
   ORDER_ID
);

/*==============================================================*/
/* Index: PRODUCT_VOUCHER_FK                                    */
/*==============================================================*/
create index PRODUCT_VOUCHER_FK on VOUCHER
(
   PRODUCT_ID
);

