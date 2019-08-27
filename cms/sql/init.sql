DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;
-- create
--
CREATE TABLE IF NOT EXISTS t_locale
(
    locale  VARCHAR(10) NOT NULL,
    CONSTRAINT t_locale_pk PRIMARY KEY (locale)
);
--
CREATE SEQUENCE s_group_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_group
(
    id          INTEGER      NOT NULL,
    change_date TIMESTAMP    NOT NULL DEFAULT now(),
    name        VARCHAR(100) NOT NULL,
    notes       VARCHAR(500) NOT NULL DEFAULT '',
    CONSTRAINT t_group_pk PRIMARY KEY (id)
);
--
CREATE SEQUENCE s_user_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_user
(
    id                 INTEGER      NOT NULL,
    change_date        TIMESTAMP    NOT NULL DEFAULT now(),
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
    approval_code      VARCHAR(50)  NOT NULL DEFAULT '',
    approved           BOOLEAN      NOT NULL DEFAULT FALSE,
    email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    failed_login_count INTEGER      NOT NULL DEFAULT 0,
    locked             BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT t_user_pk PRIMARY KEY (id)
);
--
CREATE TABLE IF NOT EXISTS t_user2group
(
    user_id  INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id),
    CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
    CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_system_right
(
    name     VARCHAR(30) NOT NULL,
    group_id INTEGER     NOT NULL,
    value    VARCHAR(20) NOT NULL,
    CONSTRAINT t_system_right_pk PRIMARY KEY (name, group_id),
    CONSTRAINT t_system_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
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
--
CREATE SEQUENCE s_file_folder_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_file_folder
(
    id              INTEGER      NOT NULL,
    creation_date   TIMESTAMP    NOT NULL DEFAULT now(),
    change_date     TIMESTAMP    NOT NULL DEFAULT now(),
    parent_id       INTEGER      NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(200) NOT NULL DEFAULT '',
    anonymous       BOOLEAN      NOT NULL DEFAULT TRUE,
    inherits_rights BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT t_file_folder_pk PRIMARY KEY (id),
    CONSTRAINT t_file_folder_fk1 FOREIGN KEY (parent_id) REFERENCES t_file_folder (id) ON DELETE CASCADE,
    CONSTRAINT t_file_folder_un1 UNIQUE (id, parent_id, name)
);
--
CREATE SEQUENCE s_file_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_file
(
    id            INTEGER      NOT NULL,
    creation_date TIMESTAMP    NOT NULL DEFAULT now(),
    change_date   TIMESTAMP    NOT NULL DEFAULT now(),
    folder_id     INTEGER      NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_name  VARCHAR(100) NOT NULL DEFAULT '',
    description   VARCHAR(200) NOT NULL DEFAULT '',
    keywords      VARCHAR(500) NOT NULL DEFAULT '',
    author_name   VARCHAR(255) NOT NULL,
    content_type  VARCHAR(255) NOT NULL DEFAULT '',
    file_size     INTEGER      NOT NULL DEFAULT 0,
    width         INTEGER      NOT NULL DEFAULT 0,
    height        INTEGER      NOT NULL DEFAULT 0,
    bytes         BYTEA        NOT NULL,
    preview_bytes BYTEA        NULL,
    CONSTRAINT t_file_pk PRIMARY KEY (id),
    CONSTRAINT t_file_fk1 FOREIGN KEY (folder_id) REFERENCES t_file_folder (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_file_folder_right
(
    folder_id INTEGER NOT NULL,
    group_id  INTEGER NOT NULL,
    value     INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT t_file_folder_right_pk PRIMARY KEY (folder_id, group_id),
    CONSTRAINT t_file_folder_right_fk1 FOREIGN KEY (folder_id) REFERENCES t_file_folder (id) ON DELETE CASCADE,
    CONSTRAINT t_file_folder_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE t_template
(
    name         VARCHAR(60)  NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    change_date  TIMESTAMP    NOT NULL DEFAULT now(),
    display_name VARCHAR(100) NOT NULL DEFAULT '',
    description  VARCHAR(255) NOT NULL DEFAULT '',
    code         TEXT         NOT NULL DEFAULT '',
    CONSTRAINT t_template_pk PRIMARY KEY (name, type)
);
--
CREATE SEQUENCE s_page_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_page
(
    id                INTEGER      NOT NULL,
    type              VARCHAR(30)  NOT NULL DEFAULT 'PageData',
    creation_date     TIMESTAMP    NOT NULL DEFAULT now(),
    change_date       TIMESTAMP    NOT NULL DEFAULT now(),
    parent_id         INTEGER      NULL,
    ranking           INTEGER      NOT NULL DEFAULT 0,
    name              VARCHAR(30)  NOT NULL,
    display_name      VARCHAR(100) NOT NULL,
    description       VARCHAR(200) NOT NULL DEFAULT '',
    keywords          VARCHAR(500) NOT NULL DEFAULT '',
    author_name       VARCHAR(255) NOT NULL,
    in_topnav         BOOLEAN      NOT NULL DEFAULT TRUE,
    in_footer         BOOLEAN      NOT NULL DEFAULT FALSE,
    anonymous         BOOLEAN      NOT NULL DEFAULT FALSE,
    inherits_rights   BOOLEAN      NOT NULL DEFAULT TRUE,
    master_type       VARCHAR(20)  NOT NULL DEFAULT 'MASTER',
    master            VARCHAR(255) NULL,
    publish_date      TIMESTAMP    NULL,
    published_content TEXT         NOT NULL DEFAULT '',
    search_content    TEXT         NOT NULL DEFAULT '',
    CONSTRAINT t_page_pk PRIMARY KEY (id),
    CONSTRAINT t_page_fk1 FOREIGN KEY (parent_id) REFERENCES t_page (id) ON DELETE CASCADE,
    CONSTRAINT t_page_fk2 FOREIGN KEY (master, master_type) REFERENCES t_template (name, type),
    CONSTRAINT t_page_un1 UNIQUE (id, parent_id, name)
);
--
CREATE TABLE IF NOT EXISTS t_template_page
(
    id            INTEGER      NOT NULL,
    template_type VARCHAR(20)  NOT NULL DEFAULT 'PAGE',
    template      VARCHAR(255) NULL,
    CONSTRAINT t_template_page_pk PRIMARY KEY (id),
    CONSTRAINT t_template_page_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE,
    CONSTRAINT t_template_page_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_page_right
(
    page_id  INTEGER     NOT NULL,
    group_id INTEGER     NOT NULL,
    value    VARCHAR(20) NOT NULL,
    CONSTRAINT t_page_right_pk PRIMARY KEY (page_id, group_id),
    CONSTRAINT t_page_right_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE,
    CONSTRAINT t_page_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE SEQUENCE s_page_part_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_page_part
(
    id            INTEGER      NOT NULL,
    type          VARCHAR(30)  NOT NULL DEFAULT 'PagePartData',
    name          VARCHAR(60)  NOT NULL DEFAULT '',
    creation_date TIMESTAMP    NOT NULL DEFAULT now(),
    change_date   TIMESTAMP    NOT NULL DEFAULT now(),
    flex_class    VARCHAR(255) NOT NULL DEFAULT '',
    CONSTRAINT t_page_part_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_template_page_part
(
    id            INTEGER      NOT NULL,
    template_type VARCHAR(20)  NOT NULL DEFAULT 'PART',
    template      VARCHAR(255) NOT NULL,
    css_classes   VARCHAR(255) NOT NULL DEFAULT '',
    script        TEXT         NOT NULL DEFAULT '',
    CONSTRAINT t_template_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_template_page_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_template_page_part_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
CREATE TABLE IF NOT EXISTS t_part_field
(
    part_id    INTEGER     NOT NULL,
    field_type VARCHAR(60) NOT NULL,
    name       VARCHAR(60) NOT NULL DEFAULT '',
    content    TEXT        NOT NULL DEFAULT '',
    CONSTRAINT t_part_field_pk PRIMARY KEY (part_id, name),
    CONSTRAINT t_part_field_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id)
);
--
CREATE TABLE IF NOT EXISTS t_page_part2page
(
    part_id  INTEGER     NOT NULL,
    page_id  INTEGER     NULL,
    section  VARCHAR(60) NOT NULL,
    position INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT t_page_part2page_pk PRIMARY KEY (part_id, page_id, section, position),
    CONSTRAINT t_page_part2page_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_page_part2page_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);
--
-- conveniance methods
--
CREATE OR REPLACE FUNCTION addPage(_parentId INTEGER, _pageId INTEGER, _ranking INTEGER, _name VARCHAR(30), _display_name VARCHAR(100),
                                   _masterTemplate VARCHAR(255),
                                   _pageTemplate VARCHAR(255)) RETURNS INTEGER AS
$$
BEGIN
    INSERT INTO t_page (id, parent_id, ranking, type, name, display_name, description, author_name, master)
    VALUES (_pageId, _parentId, _ranking, 'TemplatePageData', _name, _display_name, '', 'System', _masterTemplate);
    INSERT INTO t_template_page (id, template)
    VALUES (_pageId, _pageTemplate);
    RETURN _pageId;
END
$$ LANGUAGE plpgsql;

-- virtual all users group
INSERT INTO t_group (id, name)
VALUES (0, 'All Users');
-- global administrators
INSERT INTO t_group (id, name)
VALUES (1, 'Global Administrators');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('APPLICATION', 1, 'EDIT');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('USER', 1, 'EDIT');
-- global admins may edit content
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 1, 'EDIT');
-- invalid password!!
-- root user
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved)
VALUES (1, 'Sys', 'Admin', 'root@localhost', 'root', 'xxx', '', TRUE);
-- root is Global Administrator
INSERT INTO t_user2group (user_id, group_id)
VALUES (1, 1);
-- global approvers
INSERT INTO t_group (id, name)
VALUES (2, 'Global Approvers');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 2, 'APPROVE');
--global editors
INSERT INTO t_group (id, name)
VALUES (3, 'Global Editors');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 3, 'EDIT');
--global readers
INSERT INTO t_group (id, name)
VALUES (4, 'Global Readers');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 4, 'READ');
-- file root folder
INSERT INTO t_file_folder (id, parent_id, name, description, anonymous, inherits_rights)
VALUES (1, NULL, 'ROOT', 'File root folder', TRUE, FALSE);
-- root page
INSERT INTO t_page (id, type, parent_id, ranking, name, display_name, description, author_name, in_topnav, in_footer,
                    anonymous, inherits_rights)
VALUES (1, 'PageData', NULL, 0, '/', 'Root', 'Root node as parent for all languages branches', 'System', FALSE, FALSE, TRUE,
        FALSE);
-- timer
INSERT INTO t_timer_task (name, display_name, execution_interval, active)
VALUES ('heartbeat', 'Heartbeat Task', 'CONTINOUS', FALSE);
INSERT INTO t_timer_task (name, display_name, execution_interval, active)
VALUES ('searchindex', 'Search Index Task', 'CONTINOUS', FALSE);

