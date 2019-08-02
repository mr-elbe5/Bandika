
--
CREATE TABLE IF NOT EXISTS t_issue_page_part
(
    id           INTEGER       NOT NULL,
    project_name VARCHAR(255)  NOT NULL,
    notes        VARCHAR(2000) NOT NULL DEFAULT '',
    owner_id     INTEGER       NOT NULL DEFAULT (0),
    group_id     INTEGER       NOT NULL DEFAULT (0),
    CONSTRAINT t_issue_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_issue_page_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_issue_page_part_fk2 FOREIGN KEY (owner_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
    CONSTRAINT t_issue_page_part_fk3 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE SET DEFAULT
);
--
CREATE SEQUENCE IF NOT EXISTS s_issue_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_issue
(
    id            INTEGER       NOT NULL,
    part_id       INTEGER       NOT NULL,
    creator_id    INTEGER       NOT NULL DEFAULT 0,
    assignee_id   INTEGER       NOT NULL DEFAULT 0,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    change_date   TIMESTAMP     NOT NULL DEFAULT now(),
    due_date      TIMESTAMP     NULL,
    close_date    TIMESTAMP     NULL,
    work_state    VARCHAR(20)   NOT NULL,
    title         VARCHAR(60)   NOT NULL,
    issue         VARCHAR(200)  NOT NULL,
    description   VARCHAR(2000) NOT NULL,
    CONSTRAINT t_issue_pk PRIMARY KEY (id),
    CONSTRAINT t_issue_fk1 FOREIGN KEY (part_id) REFERENCES t_issue_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_issue_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id),
    CONSTRAINT t_issue_fk3 FOREIGN KEY (assignee_id) REFERENCES t_user (id)
);
--
CREATE SEQUENCE IF NOT EXISTS s_issue_entry_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_issue_entry
(
    id            INTEGER       NOT NULL,
    issue_id      INTEGER       NOT NULL,
    creator_id    INTEGER       NOT NULL,
    assignee_id   INTEGER       NOT NULL,
    creation_date TIMESTAMP     NOT NULL DEFAULT now(),
    work_state    VARCHAR(20)   NOT NULL,
    entry         VARCHAR(2000) NOT NULL,
    CONSTRAINT t_issue_entry_pk PRIMARY KEY (id),
    CONSTRAINT t_issue_entry_fk1 FOREIGN KEY (issue_id) REFERENCES t_issue (id) ON DELETE CASCADE,
    CONSTRAINT t_issue_entry_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id),
    CONSTRAINT t_issue_entry_fk3 FOREIGN KEY (assignee_id) REFERENCES t_user (id)
);
--
CREATE SEQUENCE IF NOT EXISTS s_issue_file_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_issue_file
(
    id             INTEGER      NOT NULL,
    creation_date  TIMESTAMP    NOT NULL DEFAULT now(),
    change_date    TIMESTAMP    NOT NULL DEFAULT now(),
    issue_entry_id INTEGER      NOT NULL,
    name           VARCHAR(100) NOT NULL,
    author_name    VARCHAR(255) NOT NULL,
    content_type   VARCHAR(255) NOT NULL DEFAULT '',
    file_size      INTEGER      NOT NULL DEFAULT 0,
    width          INTEGER      NOT NULL DEFAULT 0,
    height         INTEGER      NOT NULL DEFAULT 0,
    bytes          BYTEA        NOT NULL,
    preview_bytes  BYTEA        NULL,
    CONSTRAINT t_issue_file_pk PRIMARY KEY (id),
    CONSTRAINT t_issue_file_fk1 FOREIGN KEY (issue_entry_id) REFERENCES t_issue_entry (id) ON DELETE CASCADE
);
--
