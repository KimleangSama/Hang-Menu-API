create table stores
(
    id               uuid         not null
        primary key,
    created_at       timestamp(6),
    created_by       uuid,
    updated_at       timestamp(6),
    updated_by       uuid,
    banner           varchar(255),
    color            varchar(255),
    description      varchar(255),
    email            varchar(255),
    expired_at       timestamp(6),
    facebook         varchar(255),
    instagram        varchar(255),
    is_archived      boolean,
    lat              double precision,
    layout           varchar(255),
    lng              double precision,
    logo             varchar(255),
    name             varchar(255) not null
        constraint ukki78gykrclic213ssuw4s7xq9
            unique,
    phone            varchar(255),
    physical_address varchar(255),
    promotion        varchar(255),
    show_google_map  boolean,
    slug             varchar(255) not null
        constraint uk5keqpfxnufhwlsn1c2eg9kbx2
            unique,
    telegram         varchar(255),
    virtual_address  varchar(255),
    website          varchar(255),
    group_id         uuid
        constraint ukiuwp3nhdhgj07a18qm2vlh9cl
            unique
        constraint fkfymk9mqeegtsw2dluhx1xnsjy
            references groups,
    extend_reason    varchar(255)
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
