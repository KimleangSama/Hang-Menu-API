CREATE TABLE stores
(
    id               UUID         NOT NULL,
    created_by       UUID,
    updated_by       UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    name             VARCHAR(255) NOT NULL,
    slug             VARCHAR(255) NOT NULL,
    logo             VARCHAR(255),
    color            VARCHAR(255),
    description      VARCHAR(255),
    physical_address VARCHAR(255),
    virtual_address  VARCHAR(255),
    phone            VARCHAR(255),
    email            VARCHAR(255),
    website          VARCHAR(255),
    facebook         VARCHAR(255),
    telegram         VARCHAR(255),
    instagram        VARCHAR(255),
    promotion        VARCHAR(255),
    banner           VARCHAR(255),
    layout           VARCHAR(255),
    lat              DOUBLE PRECISION,
    lng              DOUBLE PRECISION,
    show_google_map  BOOLEAN,
    group_id         UUID         NOT NULL,
    CONSTRAINT pk_stores PRIMARY KEY (id)
);

CREATE TABLE fee_ranges
(
    id                 UUID NOT NULL,
    condition          VARCHAR(255),
    fee                DOUBLE PRECISION,
    ordering_option_id UUID,
    CONSTRAINT pk_fee_ranges PRIMARY KEY (id)
);

CREATE TABLE feedbacks
(
    id         UUID NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    store_id   UUID,
    fullname   VARCHAR(255),
    phone      VARCHAR(255),
    comment    VARCHAR(255),
    rating     VARCHAR(255),
    CONSTRAINT pk_feedbacks PRIMARY KEY (id)
);

CREATE TABLE languages
(
    id       UUID NOT NULL,
    code     VARCHAR(255),
    name     VARCHAR(255),
    store_id UUID,
    CONSTRAINT pk_languages PRIMARY KEY (id)
);

CREATE TABLE operating_hours
(
    id         UUID NOT NULL,
    day        VARCHAR(255),
    open_time  VARCHAR(255),
    close_time VARCHAR(255),
    store_id   UUID,
    CONSTRAINT pk_operating_hours PRIMARY KEY (id)
);

CREATE TABLE ordering_options
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    store_id    UUID,
    CONSTRAINT pk_ordering_options PRIMARY KEY (id)
);

CREATE TABLE payment_methods
(
    id       UUID NOT NULL,
    method   VARCHAR(255),
    store_id UUID,
    CONSTRAINT pk_payment_methods PRIMARY KEY (id)
);

ALTER TABLE feedbacks
    ADD CONSTRAINT FK_FEEDBACKS_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE fee_ranges
    ADD CONSTRAINT FK_FEE_RANGES_ON_ORDERING_OPTION FOREIGN KEY (ordering_option_id) REFERENCES ordering_options (id);

ALTER TABLE languages
    ADD CONSTRAINT FK_LANGUAGES_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE ordering_options
    ADD CONSTRAINT FK_ORDERING_OPTIONS_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE payment_methods
    ADD CONSTRAINT FK_PAYMENT_METHODS_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE operating_hours
    ADD CONSTRAINT FK_OPERATING_HOURS_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE stores
    ADD CONSTRAINT uc_stores_group UNIQUE (group_id);

ALTER TABLE stores
    ADD CONSTRAINT uc_stores_name UNIQUE (name);

ALTER TABLE stores
    ADD CONSTRAINT uc_stores_slug UNIQUE (slug);

ALTER TABLE stores
    ADD CONSTRAINT FK_STORES_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

CREATE INDEX idx_store_name ON stores (name);

CREATE INDEX idx_store_slug ON stores (slug);
