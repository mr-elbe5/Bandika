
CREATE SEQUENCE IF NOT EXISTS s_company_id START 1000;

CREATE TABLE IF NOT EXISTS t_company
(
    id            INTEGER       NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    name          VARCHAR(255)  NOT NULL,
    street        VARCHAR(255)  NOT NULL,
    zipCode       VARCHAR(20)   NOT NULL,
    city          VARCHAR(255)  NOT NULL,
    country       VARCHAR(255)  NOT NULL DEFAULT '',
    email         VARCHAR(255)  NOT NULL,
    phone         VARCHAR(50)   NOT NULL DEFAULT '',
    fax           VARCHAR(50)   NOT NULL DEFAULT '',
    description   VARCHAR(2000) NOT NULL DEFAULT '',
    CONSTRAINT t_company_pk PRIMARY KEY (id)
);

CREATE SEQUENCE s_group_id START 1000;
CREATE TABLE IF NOT EXISTS t_group
(
    id          INTEGER      NOT NULL,
    change_date TIMESTAMP    NOT NULL DEFAULT now(),
    name        VARCHAR(100) NOT NULL,
    notes       VARCHAR(500) NOT NULL DEFAULT '',
    CONSTRAINT t_group_pk PRIMARY KEY (id)
);

CREATE SEQUENCE s_user_id START 1000;
CREATE TABLE IF NOT EXISTS t_user
(
    id                 INTEGER      NOT NULL,
    change_date        TIMESTAMP    NOT NULL DEFAULT now(),
    company_id         INTEGER      NULL,
    title              VARCHAR(30)  NOT NULL DEFAULT '',
    first_name         VARCHAR(100) NOT NULL DEFAULT '',
    last_name          VARCHAR(100) NOT NULL,
    street             VARCHAR(100) NOT NULL DEFAULT '',
    zipCode            VARCHAR(16)  NOT NULL DEFAULT '',
    city               VARCHAR(50)  NOT NULL DEFAULT '',
    country            VARCHAR(50)  NOT NULL DEFAULT '',
    email              VARCHAR(100) NOT NULL DEFAULT '',
    phone              VARCHAR(50)  NOT NULL DEFAULT '',
    fax                VARCHAR(50)  NOT NULL DEFAULT '',
    mobile             VARCHAR(50)  NOT NULL DEFAULT '',
    notes              VARCHAR(500) NOT NULL DEFAULT '',
    portrait_name      VARCHAR(255) NOT NULL DEFAULT '',
    portrait           BYTEA        NULL,
    login              VARCHAR(30)  NOT NULL,
    pwd                VARCHAR(100) NOT NULL,
    token              VARCHAR(100) NOT NULL DEFAULT '',
    locked             BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT t_user_pk PRIMARY KEY (id),
    CONSTRAINT t_user_fk1 FOREIGN KEY (company_id) REFERENCES t_company (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS t_user2group
(
    user_id  INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT t_user2group_pk PRIMARY KEY (user_id,
                                            group_id),
    CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
    CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_system_right
(
    name     VARCHAR(30) NOT NULL,
    group_id INTEGER     NOT NULL,
    CONSTRAINT t_system_right_pk PRIMARY KEY (name,
                                              group_id),
    CONSTRAINT t_system_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_timer_task
(
    name               VARCHAR(60)  NOT NULL,
    display_name       VARCHAR(255) NOT NULL,
    execution_interval VARCHAR(30)  NOT NULL,
    day                INTEGER      NOT NULL DEFAULT 0,
    hour               INTEGER      NOT NULL DEFAULT 0,
    minute             INTEGER      NOT NULL DEFAULT 0,
    last_execution     TIMESTAMP    NULL,
    note_execution     BOOLEAN      NOT NULL DEFAULT FALSE,
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT t_timer_task_pk PRIMARY KEY (name)
);

CREATE SEQUENCE IF NOT EXISTS s_content_id START 1000;

CREATE TABLE IF NOT EXISTS t_content
(
    id            INTEGER       NOT NULL,
    type          VARCHAR(30)   NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    parent_id     INTEGER       NULL,
    ranking       INTEGER       NOT NULL DEFAULT 0,
    name          VARCHAR(60)   NOT NULL,
    display_name  VARCHAR(100)  NOT NULL,
    description   VARCHAR(2000) NOT NULL DEFAULT '',
    creator_id    INTEGER       NOT NULL DEFAULT 1,
    changer_id    INTEGER       NOT NULL DEFAULT 1,
    access_type   VARCHAR(10)   NOT NULL DEFAULT 'OPEN',
    nav_type      VARCHAR(10)   NOT NULL DEFAULT 'NONE',
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT t_content_pk PRIMARY KEY (id),
    CONSTRAINT t_content_fk1 FOREIGN KEY (parent_id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_content_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
    CONSTRAINT t_content_fk3 FOREIGN KEY (changer_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
    CONSTRAINT t_content_un1 UNIQUE (id, parent_id, name)
);

CREATE SEQUENCE IF NOT EXISTS s_file_id START 1000;

CREATE TABLE IF NOT EXISTS t_file
(
    id            INTEGER       NOT NULL,
    type          VARCHAR(30)   NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    parent_id     INTEGER       NULL,
    file_name     VARCHAR(60)   NOT NULL,
    display_name  VARCHAR(100)  NOT NULL,
    description   VARCHAR(2000) NOT NULL DEFAULT '',
    creator_id    INTEGER       NOT NULL DEFAULT 1,
    changer_id    INTEGER       NOT NULL DEFAULT 1,
    content_type  VARCHAR(255)  NOT NULL DEFAULT '',
    file_size     INTEGER       NOT NULL DEFAULT 0,
    bytes         BYTEA         NOT NULL,
    CONSTRAINT t_file_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_image
(
    id            INTEGER       NOT NULL,
    width         INTEGER       NOT NULL DEFAULT 0,
    height        INTEGER       NOT NULL DEFAULT 0,
    preview_bytes BYTEA         NULL,
    CONSTRAINT t_image_pk PRIMARY KEY (id),
    CONSTRAINT t_image_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE
);

CREATE OR REPLACE VIEW v_preview_file as (
                                         select t_file.id,file_name,content_type,preview_bytes
                                         from t_file, t_image
                                         where t_file.id=t_image.id
                                             );

CREATE TABLE IF NOT EXISTS t_content_right
(
    content_id INTEGER     NOT NULL,
    group_id   INTEGER     NOT NULL,
    value      VARCHAR(20) NOT NULL,
    CONSTRAINT t_content_right_pk PRIMARY KEY (content_id, group_id),
    CONSTRAINT t_content_right_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE,
    CONSTRAINT t_content_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_content_log
(
    content_id INTEGER     NOT NULL,
    day        DATE        NOT NULL,
    count      INTEGER 	   NOT NULL,
    CONSTRAINT t_content_log_pk PRIMARY KEY (content_id, day),
    CONSTRAINT t_content_log_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE
);

-- root user
INSERT INTO t_user (id,first_name,last_name,email,login,pwd)
VALUES (1,'System','Administrator','root@localhost','root','');

-- timer
INSERT INTO t_timer_task (name,display_name,execution_interval,minute,active)
VALUES ('heartbeat','Heartbeat Task','CONTINOUS',5,FALSE);
INSERT INTO t_timer_task (name,display_name,execution_interval,minute,active)
VALUES ('cleanup','Cleanup Task','CONTINOUS',5,FALSE);

CREATE TABLE IF NOT EXISTS t_page
(
    id            INTEGER       NOT NULL,
    keywords      VARCHAR(500)  NOT NULL DEFAULT '',
    layout        VARCHAR(255) NOT NULL default 'defaultPage',
    publish_date  TIMESTAMP     NULL,
    published_content TEXT      NOT NULL DEFAULT '',
    CONSTRAINT t_page_pk PRIMARY KEY (id),
    CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION ADDPAGE (id INTEGER,parent_id INTEGER,name VARCHAR,display_name VARCHAR,
                                    description VARCHAR,user_id INTEGER, layout VARCHAR)
    RETURNS VOID AS
$$
BEGIN
    INSERT INTO t_content (id,type,parent_id,ranking,name,display_name,description,creator_id,changer_id,access_type,nav_type)
    VALUES (id,'PageData',parent_id,0,name,display_name,description,user_id,user_id,'OPEN','HEADER');
    INSERT INTO t_page (id, keywords, layout)
    VALUES (id, '', layout);
END
$$
    LANGUAGE plpgsql;

CREATE SEQUENCE s_page_part_id START 1000;

CREATE TABLE IF NOT EXISTS t_page_part
(
    id            INTEGER      NOT NULL,
    type          VARCHAR(30)  NOT NULL DEFAULT 'LayoutPartData',
    page_id       INTEGER      NULL,
    section       VARCHAR(60)  NOT NULL,
    position      INTEGER      NOT NULL DEFAULT 0,
    name          VARCHAR(60)  NOT NULL DEFAULT '',
    creation_date TIMESTAMP    NOT NULL DEFAULT now(),
    change_date   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT t_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_layout_part
(
    id            INTEGER      NOT NULL,
    layout        VARCHAR(255) NOT NULL,
    CONSTRAINT t_layout_part_pk PRIMARY KEY (id),
    CONSTRAINT t_layout_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_part_field
(
    part_id    INTEGER     NOT NULL,
    field_type VARCHAR(60) NOT NULL,
    name       VARCHAR(60) NOT NULL DEFAULT '',
    content    TEXT        NOT NULL DEFAULT '',
    CONSTRAINT t_part_field_pk PRIMARY KEY (part_id, name),
    CONSTRAINT t_part_field_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE
);

--- set pwd 'pass' dependent on salt V3xfgDrxdl8=
-- root user
update t_user set pwd='A0y3+ZmqpMhWA21VFQMkyY6v74Y=' where id=1;

-- de
SELECT ADDPAGE(1, null, 'home', 'Home', 'Home Page', 1, 'defaultPage');
update t_page set publish_date=now(), published_content='' where id=1;
