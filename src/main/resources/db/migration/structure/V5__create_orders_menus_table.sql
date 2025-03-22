CREATE TABLE favorites
(
    id         UUID NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    UUID NOT NULL,
    menu_id    UUID NOT NULL,
    CONSTRAINT pk_favorites PRIMARY KEY (id)
);

CREATE TABLE order_menus
(
    id               UUID             NOT NULL,
    created_by       UUID,
    updated_by       UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    order_id         UUID,
    menu_id          UUID,
    code             VARCHAR(255),
    name             VARCHAR(255),
    image            VARCHAR(255),
    description      VARCHAR(255),
    price            DOUBLE PRECISION NOT NULL,
    discount         DOUBLE PRECISION NOT NULL,
    currency         VARCHAR(255),
    quantity         INTEGER          NOT NULL,
    special_requests VARCHAR(255),
    CONSTRAINT pk_order_menus PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id                     UUID NOT NULL,
    created_by             UUID,
    updated_by             UUID,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    code                   UUID,
    total_amount_in_riel   DOUBLE PRECISION,
    total_amount_in_dollar DOUBLE PRECISION,
    status                 VARCHAR(255),
    store_id               UUID,
    order_time             TIMESTAMP WITHOUT TIME ZONE,
    special_instructions   VARCHAR(255),
    phone_number           VARCHAR(255),
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE translations
(
    id            UUID       NOT NULL,
    menu_id       UUID       NOT NULL,
    language_code VARCHAR(5) NOT NULL,
    name          VARCHAR(255),
    description   VARCHAR(255),
    CONSTRAINT pk_translations PRIMARY KEY (id)
);

ALTER TABLE favorites
    ADD CONSTRAINT uc_7e38532b24355b11d25d79494 UNIQUE (user_id, menu_id);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_code UNIQUE (code);

ALTER TABLE favorites
    ADD CONSTRAINT FK_FAVORITES_ON_MENU FOREIGN KEY (menu_id) REFERENCES menus (id);

ALTER TABLE favorites
    ADD CONSTRAINT FK_FAVORITES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE order_menus
    ADD CONSTRAINT FK_ORDER_MENUS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE translations
    ADD CONSTRAINT FK_TRANSLATIONS_ON_MENU FOREIGN KEY (menu_id) REFERENCES menus (id);
