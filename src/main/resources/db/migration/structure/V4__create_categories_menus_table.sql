CREATE TABLE categories
(
    id           UUID         NOT NULL,
    created_by   UUID,
    updated_by   UUID,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    icon         VARCHAR(255),
    is_hidden    BOOLEAN      NOT NULL,
    is_available BOOLEAN      NOT NULL,
    position     INTEGER      NOT NULL,
    store_id     UUID,
    group_id     UUID         NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE menu_badges
(
    menu_id UUID NOT NULL,
    badges  VARCHAR(255)
);

CREATE TABLE menu_images
(
    id      UUID NOT NULL,
    name    VARCHAR(255),
    url     VARCHAR(255),
    menu_id UUID,
    CONSTRAINT pk_menu_images PRIMARY KEY (id)
);

CREATE TABLE menus
(
    id          UUID NOT NULL,
    created_by  UUID,
    updated_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    code        VARCHAR(255),
    name        VARCHAR(255),
    description VARCHAR(255),
    price       DOUBLE PRECISION,
    discount    DOUBLE PRECISION,
    currency    VARCHAR(255),
    image       VARCHAR(255),
    is_hidden   BOOLEAN,
    category_id UUID,
    group_id    UUID NOT NULL,
    CONSTRAINT pk_menus PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_731b2dd0c5f5c3f2add6960b2 UNIQUE (name, store_id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_STORE FOREIGN KEY (store_id) REFERENCES stores (id);

ALTER TABLE menu_images
    ADD CONSTRAINT uc_dac12e29a6f76eb142ab111de UNIQUE (url);

ALTER TABLE menus
    ADD CONSTRAINT FK_MENUS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE menus
    ADD CONSTRAINT FK_MENUS_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

CREATE INDEX idx_menu_name ON menus (name);
CREATE INDEX idx_menu_category ON menus (category_id);

ALTER TABLE menu_images
    ADD CONSTRAINT FK_MENU_IMAGES_ON_MENU FOREIGN KEY (menu_id) REFERENCES menus (id);

ALTER TABLE menu_badges
    ADD CONSTRAINT fk_menu_badges_on_menu FOREIGN KEY (menu_id) REFERENCES menus (id);

CREATE INDEX idx_menus_category ON menus (category_id);
CREATE INDEX idx_menus_name ON menus (name);

