CREATE TABLE groups
(
    id          UUID NOT NULL,
    created_by  UUID,
    updated_by  UUID,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

CREATE TABLE groups_members
(
    id       UUID NOT NULL,
    group_id UUID,
    user_id  UUID,
    CONSTRAINT pk_groups_members PRIMARY KEY (id)
);


ALTER TABLE groups_members
    ADD CONSTRAINT FK_GROUPS_MEMBERS_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE groups_members
    ADD CONSTRAINT FK_GROUPS_MEMBERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE groups_members
    ADD CONSTRAINT uc_485c55904490151bcd55da852 UNIQUE (user_id);

ALTER TABLE groups_members
    ADD CONSTRAINT uc_c46cd57b8d4619aa98311da17 UNIQUE (group_id, user_id);

INSERT INTO groups (id, created_by, updated_by, created_at, updated_at, name, description)
VALUES ('f9323a07-9625-4630-b8eb-8b6669aff356', 'f9323a07-9625-4630-b8eb-8b6669aff356',
        'f9323a07-9625-4630-b8eb-8b6669aff356', '2025-03-22 07:53:15.000000', '2025-03-22 07:53:17.000000',
        'superadmin_randomizer', 'superadmin');

INSERT INTO groups_members (id, group_id, user_id)
VALUES ('b41869b9-3407-4c78-a38f-1936a4c8b9db', 'f9323a07-9625-4630-b8eb-8b6669aff356',
        'b41869b9-3407-4c78-a38f-1936a4c8b9db');
