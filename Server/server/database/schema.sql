/*==============================================================*/
/* DBMS name:      SQLite                                        */
/* Created on:     17/03/2024 12:32:15                          */
/*==============================================================*/

PRAGMA foreign_keys = ON;

DROP INDEX IF EXISTS CAFETERIAPRODUCT_CAFETERIAORDER_FK;
DROP INDEX IF EXISTS CUSTOMER_CAFETERIAORDER_FK;
DROP TABLE IF EXISTS CAFETERIA_ORDER;
DROP TABLE IF EXISTS CAFETERIA_PRODUCT;
DROP INDEX IF EXISTS CUSTOMER_CREDITCARD_FK;
DROP TABLE IF EXISTS CREDIT_CARD;
DROP TABLE IF EXISTS CUSTOMER;
DROP TABLE IF EXISTS EVENT;
DROP INDEX IF EXISTS CUSTOMER_PURCHASE_FK;
DROP TABLE IF EXISTS PURCHASE;
DROP INDEX IF EXISTS EVENT_TICKET_FK;
DROP INDEX IF EXISTS PURCHASE_TICKET_FK;
DROP TABLE IF EXISTS TICKET;
DROP INDEX IF EXISTS CAFETERIAORDER_VOUCHER_FK;
DROP INDEX IF EXISTS CUSTOMER_VOUCHER_FK;
DROP TABLE IF EXISTS VOUCHER;

/*==============================================================*/
/* Table: CAFETERIA_ORDER                                       */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS CAFETERIA_ORDER
(
    CUSTOMER_ID TEXT    NOT NULL,
    PRODUCT_ID  INTEGER NOT NULL,
    ORDER_ID    INTEGER NOT NULL,
    DATE        TEXT,
    PAID        INTEGER,
    PICKED_UP   INTEGER,
    TOTAL_PRICE REAL,
    PRIMARY KEY (CUSTOMER_ID, PRODUCT_ID, ORDER_ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES CAFETERIA_PRODUCT (PRODUCT_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER (CUSTOMER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*==============================================================*/
/* Index: CUSTOMER_CAFETERIAORDER_FK                            */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CUSTOMER_CAFETERIAORDER_FK ON CAFETERIA_ORDER (CUSTOMER_ID);

/*==============================================================*/
/* Index: CAFETERIAPRODUCT_CAFETERIAORDER_FK                    */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CAFETERIAPRODUCT_CAFETERIAORDER_FK ON CAFETERIA_ORDER (PRODUCT_ID);

/*==============================================================*/
/* Table: CAFETERIA_PRODUCT                                     */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS CAFETERIA_PRODUCT
(
    PRODUCT_ID  INTEGER NOT NULL PRIMARY KEY,
    AVAILABLE   INTEGER,
    NAME        TEXT,
    DESCRIPTION TEXT,
    PRICE       REAL
);

/*==============================================================*/
/* Table: CREDIT_CARD                                           */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS CREDIT_CARD
(
    CREDIT_CARD_ID INTEGER NOT NULL PRIMARY KEY,
    CUSTOMER_ID    TEXT    NOT NULL,
    TYPE           TEXT,
    NUMBER         TEXT,
    VALIDITY       TEXT,
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER (CUSTOMER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*==============================================================*/
/* Index: CUSTOMER_CREDITCARD_FK                                */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CUSTOMER_CREDITCARD_FK ON CREDIT_CARD (CUSTOMER_ID);

/*==============================================================*/
/* Table: CUSTOMER                                              */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS CUSTOMER
(
    CUSTOMER_ID TEXT NOT NULL PRIMARY KEY,
    USERNAME    TEXT,
    PASSWORD    TEXT,
    TAX_NUMBER  INTEGER,
    PUBLIC_KEY  TEXT,
    NAME        TEXT
);

/*==============================================================*/
/* Table: EVENT                                                 */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS EVENT
(
    EVENT_ID TEXT NOT NULL PRIMARY KEY,
    NAME     TEXT,
    DATE     TEXT,
    PICTURE  BLOB,
    PRICE    REAL
);

/*==============================================================*/
/* Table: PURCHASE                                              */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS PURCHASE
(
    PURCHASE_ID TEXT NOT NULL PRIMARY KEY,
    CUSTOMER_ID TEXT NOT NULL,
    DATE        TEXT,
    TOTAL_PRICE REAL,
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER (CUSTOMER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*==============================================================*/
/* Index: CUSTOMER_PURCHASE_FK                                  */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CUSTOMER_PURCHASE_FK ON PURCHASE (CUSTOMER_ID);

/*==============================================================*/
/* Table: TICKET                                                */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS TICKET
(
    TICKET_ID     TEXT NOT NULL PRIMARY KEY,
    PURCHASE_ID   TEXT NOT NULL,
    EVENT_ID      TEXT NOT NULL,
    PURCHASE_DATE TEXT,
    USED          INTEGER,
    QRCODE        TEXT,
    PLACE         TEXT,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENT (EVENT_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (PURCHASE_ID) REFERENCES PURCHASE (PURCHASE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*==============================================================*/
/* Index: PURCHASE_TICKET_FK                                    */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS PURCHASE_TICKET_FK ON TICKET (PURCHASE_ID);

/*==============================================================*/
/* Index: EVENT_TICKET_FK                                       */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS EVENT_TICKET_FK ON TICKET (EVENT_ID);

/*==============================================================*/
/* Table: VOUCHER                                               */
/*==============================================================*/
CREATE TABLE IF NOT EXISTS VOUCHER
(
    VOUCHER_ID      TEXT NOT NULL PRIMARY KEY,
    CUSTOMER_ID     TEXT NOT NULL,
    CAF_CUSTOMER_ID TEXT,
    PRODUCT_ID      INTEGER,
    ORDER_ID        INTEGER,
    TYPE            TEXT,
    DESCRIPTION     TEXT,
    REDEEMED        INTEGER,
    FOREIGN KEY (CAF_CUSTOMER_ID, PRODUCT_ID, ORDER_ID) REFERENCES CAFETERIA_ORDER (CUSTOMER_ID, PRODUCT_ID, ORDER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER (CUSTOMER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

/*==============================================================*/
/* Index: CUSTOMER_VOUCHER_FK                                   */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CUSTOMER_VOUCHER_FK ON VOUCHER (CUSTOMER_ID);

/*==============================================================*/
/* Index: CAFETERIAORDER_VOUCHER_FK                             */
/*==============================================================*/
CREATE INDEX IF NOT EXISTS CAFETERIAORDER_VOUCHER_FK ON VOUCHER (CAF_CUSTOMER_ID, PRODUCT_ID, ORDER_ID);
