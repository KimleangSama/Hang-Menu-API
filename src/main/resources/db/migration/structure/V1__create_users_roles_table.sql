create table users
(
    id                 uuid         not null
        primary key,
    created_at         timestamp(6),
    created_by         uuid,
    updated_at         timestamp(6),
    updated_by         uuid,
    address            varchar(255),
    deleted_at         timestamp(6),
    deleted_by         uuid,
    email              varchar(255)
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    emergency_contact  varchar(255),
    emergency_relation varchar(255),
    fullname           varchar(255),
    last_login_at      timestamp(6),
    password           varchar(255) not null,
    phone              varchar(255)
        constraint ukdu5v5sr43g5bfnji4vb8hg5s3
            unique,
    profile_url        varchar(255),
    provider           varchar(255) not null
        constraint users_provider_check
            check ((provider)::text = ANY
                   ((ARRAY ['local'::character varying, 'facebook'::character varying, 'google'::character varying, 'github'::character varying])::text[])),
    raw                varchar(255) not null,
    status             varchar(255) not null
        constraint users_status_check
            check ((status)::text = ANY
                   ((ARRAY ['active'::character varying, 'pending'::character varying, 'inactive'::character varying, 'blocked'::character varying, 'deleted'::character varying])::text[])),
    username           varchar(255) not null
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique
);

CREATE TABLE roles
(
    id   UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE roles
    ADD CONSTRAINT unq_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_8eba69f9c904252e3321f0bae UNIQUE (phone);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_77584fbe74cc86922be2a3560 UNIQUE (username);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

INSERT INTO users (id, created_by, updated_by, created_at, updated_at, username, password, raw, fullname, email, phone,
                   address, emergency_contact, emergency_relation, profile_url, last_login_at, provider, status,
                   deleted_at, deleted_by)

VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9db', null, null, '2025-03-21 21:36:52.603573', '2025-03-21 21:36:52.603595',
        'superadmin', '$2a$10$Oet966h8Aimau6Eu0p5gYufsj4gTVFlz7aE9J85YhWyzTouY6T8ym', 'superadmin@_@', 'superadmin',
        'superadmin', null, null, null, null, null, null, 'local', 'pending', null, null);

INSERT INTO roles (id, name)
VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9db', 'admin');
INSERT INTO roles (id, name)
VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9da', 'manager');

INSERT INTO users_roles (role_id, user_id)
VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9db', 'b41869b9-3407-4c78-a38f-1936a4c8b9db');
INSERT INTO users_roles (role_id, user_id)
VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9da', 'b41869b9-3407-4c78-a38f-1936a4c8b9db');
