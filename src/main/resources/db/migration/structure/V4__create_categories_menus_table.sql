create table categories
(
    id           uuid         not null
        primary key,
    created_at   timestamp(6),
    created_by   uuid,
    updated_at   timestamp(6),
    updated_by   uuid,
    description  varchar(255),
    icon         varchar(255),
    is_available boolean      not null,
    is_hidden    boolean      not null,
    name         varchar(255) not null,
    position     integer      not null,
    group_id     uuid         not null
        constraint fk36j4852qk859jhclfoocn6i7k
            references groups,
    store_id     uuid
        constraint fkswh8ov7e46aj6bf053xla2c6b
            references stores,
    constraint uk9ov5tvnnyqbsej9s8cuww53aa
        unique (name, store_id)
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

create table menus
(
    id           uuid not null
        primary key,
    created_at   timestamp(6),
    created_by   uuid,
    updated_at   timestamp(6),
    updated_by   uuid,
    code         varchar(255),
    currency     varchar(255),
    description  varchar(255),
    discount     double precision,
    image        varchar(255),
    is_available boolean,
    name         varchar(255),
    price        double precision,
    category_id  uuid
        constraint fkp0a1nfv0qt1ftw07yd8h00ud7
            references categories,
    group_id     uuid
        constraint fksikep6sn85nnxo7563v28oo4l
            references groups
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

